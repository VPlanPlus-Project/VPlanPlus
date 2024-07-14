package es.jvbabi.vplanplus.feature.main_homework.shared.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.repository.ResponseDataWrapper
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDocumentDao
import es.jvbabi.vplanplus.data.source.database.dao.PreferredHomeworkNotificationTimeDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.data.model.DbPreferredNotificationTime
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkDocumentId
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkId
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkTaskId
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.Response
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.time.DayOfWeek
import java.time.ZonedDateTime

class HomeworkRepositoryImpl(
    private val homeworkDao: HomeworkDao,
    private val homeworkDocumentDao: HomeworkDocumentDao,
    private val homeworkNotificationTimeDao: PreferredHomeworkNotificationTimeDao,
    private val vppIdRepository: VppIdRepository,
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val context: Context
) : HomeworkRepository {

    private var isUpdateRunning = false

    override suspend fun downloadHomework(vppId: VppId?, group: Group): List<HomeworkCore.CloudHomework>? {
        if (vppId == null) vppIdNetworkRepository.authentication = group.school.buildAccess().buildVppAuthentication()
        else {
            val token = vppIdRepository.getVppIdToken(vppId) ?: return null
            vppIdNetworkRepository.authentication = BearerAuthentication(token)
        }
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${group.school.id}/group/${group.groupId}/homework",
            requestMethod = HttpMethod.Get
        )
        if (response.response != HttpStatusCode.OK || response.data == null) return null
        val data = ResponseDataWrapper.fromJson<List<HomeworkResponse>>(response.data)
        return data.mapNotNull homework@{ homework ->
            val createdBy = vppIdRepository.getVppId(homework.createdBy, group.school, false) ?: run {
                Log.e("HomeworkRepository.downloadHomework", "Failed to get VppId for id ${homework.createdBy}")
                return@homework null
            }
            HomeworkCore.CloudHomework(
                id = homework.id.toInt(),
                createdBy = createdBy,
                until = ZonedDateTimeConverter().timestampToZonedDateTime(homework.until),
                isPublic = homework.shareWithClass,
                createdAt = ZonedDateTimeConverter().timestampToZonedDateTime(homework.createdAt),
                group = group,
                defaultLesson = defaultLessonRepository.getDefaultLessonByGroupId(group.groupId).firstOrNull { it.vpId == homework.vpId },
                tasks = homework.tasks.map {
                    if (vppId != null) HomeworkTaskDone(it.id, homework.id.toInt(), it.content, it.done ?: false)
                    else HomeworkTaskCore(it.id, homework.id.toInt(), it.content)
                },
                documents = homework.documentIds.mapNotNull documents@{ documentId ->
                    downloadHomeworkDocumentMetadata(vppId, group, homework.id.toInt(), documentId) ?: return@documents null
                }
            )
        }
    }

    override suspend fun downloadHomeworkDocument(vppId: VppId?, group: Group, homeworkId: Int, homeworkDocumentId: Int): ByteArray? {
        if (vppId == null) vppIdNetworkRepository.authentication = group.school.buildAccess().buildVppAuthentication()
        else {
            val token = vppIdRepository.getVppIdToken(vppId) ?: return null
            vppIdNetworkRepository.authentication = BearerAuthentication(token)
        }
        val response = vppIdNetworkRepository.doRequestRaw(
            path = "/api/$API_VERSION/school/${group.school.id}/group/${group.groupId}/homework/$homeworkId/document/$homeworkDocumentId/content",
            requestMethod = HttpMethod.Get
        )
        if (response.response != HttpStatusCode.OK || response.data == null) return null
        return response.data
    }

    override suspend fun downloadHomeworkDocumentMetadata(vppId: VppId?, group: Group, homeworkId: Int, homeworkDocumentId: Int): HomeworkDocument? {
        if (vppId == null) vppIdNetworkRepository.authentication = group.school.buildAccess().buildVppAuthentication()
        else {
            val token = vppIdRepository.getVppIdToken(vppId) ?: return null
            vppIdNetworkRepository.authentication = BearerAuthentication(token)
        }
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${group.school.id}/group/${group.groupId}/homework/$homeworkId/document/$homeworkDocumentId",
            requestMethod = HttpMethod.Get
        )
        if (response.response != HttpStatusCode.OK || response.data == null) return null
        val data = ResponseDataWrapper.fromJson<HomeworkDocumentResponse>(response.data)
        return HomeworkDocument(
            name = data.name,
            type = HomeworkDocumentType.fromExtension(data.extension),
            documentId = homeworkDocumentId,
            homeworkId = homeworkId
        )
    }

    override suspend fun getAll(): Flow<List<HomeworkCore>> {
        return homeworkDao.getAll().map {
            it.map { homework -> homework.toCoreModel() }
        }
    }

    override suspend fun getAllByProfile(profile: ClassProfile): Flow<List<PersonalizedHomework>> {
        return homeworkDao.getByGroupId(profile.group.groupId).map {
            it.map { homework -> homework.toProfileModel(profile) }
        }
    }

    override suspend fun getHomeworkById(homeworkId: Int): Flow<HomeworkCore?> {
        return homeworkDao.getById(homeworkId).map { it?.toCoreModel() }
    }

    override suspend fun getProfileHomeworkById(homeworkId: Int, classProfile: ClassProfile): Flow<PersonalizedHomework?> {
        return homeworkDao.getById(homeworkId).map { it?.toProfileModel(classProfile) }
    }

    override suspend fun changeDueDateCloud(profileHomework: PersonalizedHomework.CloudHomework, newDate: ZonedDateTime): Unit? {
        val vppId = profileHomework.profile.vppId ?: return null
        val token = vppIdRepository.getVppIdToken(vppId) ?: return null
        if (vppId.group?.school == null) return null
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${profileHomework.homework.id}",
            requestBody = Gson().toJson(
                ChangeDueToDateRequest(
                    until = ZonedDateTimeConverter().zonedDateTimeToTimestamp(newDate),
                )
            ),
            requestMethod = HttpMethod.Patch
        )
        if (result.response != HttpStatusCode.OK) return null
        return Unit
    }

    override suspend fun changeDueDateDb(homework: HomeworkCore, newDate: ZonedDateTime) {
        homeworkDao.updateDueDate(homework.id, newDate)
    }

    override suspend fun findLocalId(): Int {
        val homework = homeworkDao.getAll().first().minByOrNull { it.homework.id }
        return minOf(homework?.homework?.id?.toInt() ?: 0, 0) - 1
    }

    override suspend fun findLocalTaskId(): Int {
        val task = homeworkDao.getAll().first().flatMap { it.tasks }.minByOrNull { it.task.id }?.toCoreModel()
        return minOf(task?.id ?: 0, 0) - 1
    }

    override suspend fun findLocalDocumentId(): Int {
        return homeworkDocumentDao.getAllHomeworkDocuments().first().minByOrNull { it.id }?.id ?: 0
    }

    override suspend fun getHomeworkByTask(taskId: Int): HomeworkCore {
        val homeworkId = homeworkDao.getHomeworkTaskById(taskId).first().homeworkId
        return homeworkDao.getById(homeworkId).first()!!.toCoreModel()
    }

    override suspend fun clearCache() {
        homeworkDao.deleteAllCloud()
        File(context.filesDir, "homework_documents").listFiles()?.forEach {
            val id = it.name.substringBefore(".").toInt()
            if (id > 0) {
                homeworkDocumentDao.deleteHomeworkDocumentById(id)
                it.delete()
            }
        }
    }

    override fun isUpdateRunning(): Boolean {
        return isUpdateRunning
    }

    override suspend fun setPreferredHomeworkNotificationTime(
        hour: Int,
        minute: Int,
        dayOfWeek: DayOfWeek
    ) {
        homeworkNotificationTimeDao.insertPreferredHomeworkNotificationTime(
            DbPreferredNotificationTime(
                dayOfWeek = dayOfWeek.value,
                hour = hour,
                minute = minute,
                overrideDefault = true
            )
        )
    }

    override suspend fun removePreferredHomeworkNotificationTime(dayOfWeek: DayOfWeek) {
        homeworkNotificationTimeDao.deletePreferredHomeworkNotificationTime(dayOfWeek.value)
    }

    override fun getPreferredHomeworkNotificationTimes() = flow {
        homeworkNotificationTimeDao.getPreferredHomeworkNotificationTime().collect { times ->
            emit(times.map { it.toModel() })
        }
    }

    override suspend fun addDocumentDb(documentId: Int?, homeworkId: Int, name: String, type: HomeworkDocumentType): HomeworkDocumentId {
        val id = documentId ?: (findLocalDocumentId() - 1)
        homeworkDocumentDao.upsertHomeworkDocument(
            DbHomeworkDocument(
                id,
                fileName = name,
                homeworkId = homeworkId.toLong(),
                fileType = type.extension
            )
        )
        return id
    }

    override suspend fun addDocumentCloud(vppId: VppId, name: String, homeworkId: Int, type: HomeworkDocumentType, content: ByteArray, onUploading: (sent: Long, total: Long) -> Unit): Response<Boolean, Int?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/$homeworkId/document",
            requestBody = content,
            requestMethod = HttpMethod.Post,
            queries = mapOf("file_name" to name + "." + type.extension),
            onUploading = { sent, of -> onUploading(sent, of) }
        )
        if (response.response?.isSuccess() != true) return Response(false, null)
        val data = response.data ?: return Response(false, null)
        return Response(false, ResponseDataWrapper.fromJson<UploadDocumentResponse>(data).id)
    }

    override suspend fun addHomeworkDb(homeworkId: Int?, clazzProfile: ClassProfile?, defaultLessonVpId: Int?, dueTo: ZonedDateTime, vppId: VppId?, isPublic: Boolean, createdAt: ZonedDateTime): HomeworkId {
        val id = homeworkId ?: (findLocalId() - 1)
        homeworkDao.insert(
            DbHomework(
                id = id.toLong(),
                groupId = if (vppId == null) clazzProfile!!.group.groupId else vppId.group!!.groupId,
                defaultLessonVpId = defaultLessonVpId,
                until = dueTo,
                isPublic = isPublic,
                owningProfileId = if (vppId == null) clazzProfile!!.id else null,
                createdBy = vppId?.id,
                createdAt = createdAt
            )
        )
        return id
    }

    override suspend fun addHomeworkCloud(vppId: VppId, dueTo: ZonedDateTime, tasks: List<String>, vpId: Int?, isPublic: Boolean): Response<Boolean, AddHomeworkResponse?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework",
            requestBody = Gson().toJson(
                AddHomeworkRequest(
                    vpId = vpId,
                    shareWithClass = isPublic,
                    until = ZonedDateTimeConverter().zonedDateTimeToTimestamp(dueTo),
                    tasks = tasks
                )
            ),
            requestMethod = HttpMethod.Post
        )
        if (response.response?.isSuccess() != true || response.data == null) return Response(false, null)
        return Response(true, ResponseDataWrapper.fromJson<AddHomeworkResponse>(response.data))
    }

    override suspend fun addTaskDb(homeworkId: Int, taskId: Int?, content: String): HomeworkTaskId {
        val id = taskId ?: (findLocalTaskId() - 1)
        homeworkDao.insertTask(
            DbHomeworkTask(
                id = id,
                homeworkId = homeworkId,
                content = content,
            )
        )
        return id
    }

    override suspend fun addTaskCloud(vppId: VppId, homeworkId: Int, content: String): Response<Boolean, Int?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/$homeworkId/task",
            requestBody = Gson().toJson(AddOrChangeTaskRequest(content)),
            requestMethod = HttpMethod.Post
        )
        if (response.response?.isSuccess() != true || response.data == null) return Response(false, null)
        return Response(true, ResponseDataWrapper.fromJson<AddTaskResponse>(response.data).id.toInt())
    }

    override suspend fun changeTaskStateCloud(vppId: VppId, homeworkTaskId: Int, isDone: Boolean): Response<Boolean, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        val homework = getHomeworkByTask(homeworkTaskId)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homework.id}/task/$homeworkTaskId",
            requestBody = Gson().toJson(MarkDoneRequest(isDone)),
            requestMethod = HttpMethod.Patch
        )
        return if (response.response?.isSuccess() == true) Response(true, Unit)
        else Response(false, null)
    }

    override suspend fun changeTaskStateDb(profile: ClassProfile, homeworkTaskId: Int, isDone: Boolean) {
        homeworkDao.getHomeworkTaskById(homeworkTaskId).first()
        homeworkDao.insertTaskDone(homeworkTaskId, profile.id, isDone)
    }

    override suspend fun changeDocumentNameCloud(vppId: VppId, homeworkDocument: HomeworkDocument, newName: String): Response<Boolean, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homeworkDocument.homeworkId}/document/${homeworkDocument.documentId}/name",
            requestBody = Gson().toJson(RenameDocumentRequest(newName)),
            requestMethod = HttpMethod.Patch
        )
        return if (response.response?.isSuccess() == true) Response(true, Unit)
        else Response(false, null)
    }

    override suspend fun changeDocumentNameDb(homeworkDocument: HomeworkDocument, newName: String) {
        homeworkDocumentDao.updateHomeworkDocumentFileName(homeworkDocument.documentId, newName)
    }

    override suspend fun deleteDocumentCloud(vppId: VppId, homeworkDocument: HomeworkDocument): Response<Boolean, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homeworkDocument.homeworkId}/document/${homeworkDocument.documentId}",
            requestMethod = HttpMethod.Delete
        )
        return if (response.response?.isSuccess() == true) Response(true, Unit)
        else Response(false, null)
    }

    override suspend fun deleteDocumentDb(homeworkDocument: HomeworkDocument) {
        homeworkDocumentDao.deleteHomeworkDocumentById(homeworkDocument.documentId)
    }

    override suspend fun changeHomeworkSharingDb(homework: HomeworkCore.CloudHomework, isPublic: Boolean) {
        homeworkDao.changePublic(homework.id, isPublic)
    }


    override suspend fun changeHomeworkSharingCloud(homeworkWithProfile: PersonalizedHomework.CloudHomework, isPublic: Boolean): Response<Boolean, Unit?> {
        val vppId = homeworkWithProfile.profile.vppId ?: return Response(false, null)
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homeworkWithProfile.homework.id}",
            requestBody = Gson().toJson(ChangeVisibilityRequest(isPublic)),
            requestMethod = HttpMethod.Patch
        )
        return if (response.response?.isSuccess() == true) Response(true, Unit)
        else Response(false, null)
    }

    override suspend fun changeHomeworkVisibilityDb(homeworkProfilePersonalizedHomework: PersonalizedHomework.CloudHomework, hide: Boolean) {
        homeworkDao.changeHidden(homeworkProfilePersonalizedHomework.homework.id, homeworkProfilePersonalizedHomework.profile.id, hide)
    }

    override suspend fun changeHomeworkVisibilityDb(homework: HomeworkCore.CloudHomework, profile: ClassProfile, hide: Boolean) {
        homeworkDao.changeHidden(homework.id, profile.id, hide)
    }

    override suspend fun deleteHomeworkDb(homework: HomeworkCore) {
        homeworkDao.deleteHomework(homework.id)
    }

    override suspend fun deleteHomeworkCloud(homeworkWithProfile: PersonalizedHomework.CloudHomework): Response<Boolean, Unit?> {
        val vppId = homeworkWithProfile.profile.vppId ?: return Response(false, null)
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homeworkWithProfile.homework.id}",
            requestMethod = HttpMethod.Delete
        )
        return if (response.response?.isSuccess() == true) Response(true, Unit)
        else Response(false, null)
    }

    override suspend fun changeTaskContentCloud(vppId: VppId, homeworkTaskCore: HomeworkTaskCore, newContent: String): Response<Boolean, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homeworkTaskCore.homeworkId}/task/${homeworkTaskCore.id}",
            requestBody = Gson().toJson(AddOrChangeTaskRequest(newContent)),
            requestMethod = HttpMethod.Patch
        )
        return if (response.response?.isSuccess() == true) Response(true, Unit)
        else Response(false, null)
    }

    override suspend fun changeTaskContentDb(homeworkTaskCore: HomeworkTaskCore, newContent: String) {
        homeworkDao.insertTask(
            DbHomeworkTask(
                id = homeworkTaskCore.id,
                homeworkId = homeworkTaskCore.homeworkId,
                content = newContent,
            )
        )
    }

    override suspend fun deleteTaskCloud(vppId: VppId, task: HomeworkTaskCore): Response<Boolean, Unit?> {
        val homework = getHomeworkByTask(task.id)
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(false, null)
        if (vppId.group?.school == null) return Response(false, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homework.id}/task/${task.id}",
            requestMethod = HttpMethod.Delete
        )
        return if (response.response?.isSuccess() == true) Response(true, Unit)
        else Response(false, null)
    }

    override suspend fun deleteTaskDb(task: HomeworkTaskCore) {
        homeworkDao.deleteTask(task.id)
    }

    override suspend fun getDocumentById(id: Int): HomeworkDocument? {
        return homeworkDocumentDao.getHomeworkDocumentById(id)?.toModel()
    }
}

private data class UploadDocumentResponse(
    @SerializedName("id") val id: Int
)

private data class HomeworkResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("vp_id") val vpId: Int,
    @SerializedName("due_to") val until: Long,
    @SerializedName("created_by") val createdBy: Long,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("is_public") val shareWithClass: Boolean,
    @SerializedName("tasks") val tasks: List<HomeRecordTask>,
    @SerializedName("documents") val documentIds: List<Int>
)

private data class HomeRecordTask @JvmOverloads constructor(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val content: String,
    @SerializedName("is_done") val done: Boolean? = null
)

private data class MarkDoneRequest(
    @SerializedName("done") val done: Boolean
)

private data class AddOrChangeTaskRequest(
    @SerializedName("description") val content: String
)

private data class AddHomeworkRequest(
    @SerializedName("vp_id") val vpId: Int?,
    @SerializedName("is_public") val shareWithClass: Boolean,
    @SerializedName("due_to") val until: Long,
    @SerializedName("tasks") val tasks: List<String>
)

data class AddHomeworkResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("tasks") val tasks: List<AddHomeworkResponseTask>,
)

data class AddHomeworkResponseTask(
    @SerializedName("id") val id: Int,
    @SerializedName("hash") val contentSHA256: String
)

private data class AddTaskResponse(
    @SerializedName("id") val id: Long,
)

private data class ChangeVisibilityRequest(
    @SerializedName("is_public") var shared: Boolean,
)

private data class ChangeDueToDateRequest(
    @SerializedName("due_to") val until: Long
)

private data class HomeworkDocumentResponse(
    @SerializedName("file_name") val name: String,
    @SerializedName("file_type") val extension: String,
    @SerializedName("id") val id: Int
)

private data class RenameDocumentRequest(
    @SerializedName("file_name") val name: String
)
package es.jvbabi.vplanplus.feature.main_homework.shared.data.repository

import android.content.Context
import android.net.Uri
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
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.Document
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkDocumentId
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkId
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkTaskId
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.NewTaskRecord
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.Response
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.util.sha256
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
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

    override suspend fun downloadHomework(vppId: VppId?, group: Group): List<CloudHomework>? {
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
            CloudHomework(
                id = homework.id,
                createdBy = createdBy,
                until = ZonedDateTimeConverter().timestampToZonedDateTime(homework.until),
                isHidden = false,
                isPublic = homework.shareWithClass,
                createdAt = ZonedDateTimeConverter().timestampToZonedDateTime(homework.createdAt),
                group = group,
                defaultLesson = defaultLessonRepository.getDefaultLessonByGroupId(group.groupId).firstOrNull { it.vpId == homework.vpId },
                tasks = homework.tasks.map { HomeworkTask(it.id, it.content, it.done ?: false, homework.id.toInt()) },
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

    override suspend fun downloadHomeworkDocumentMetadata(vppId: VppId?, group: Group, homeworkId: Int, homeworkDocumentId: Int): es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument? {
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
        return es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument(
            name = data.name,
            type = HomeworkDocumentType.fromExtension(data.extension),
            documentId = homeworkDocumentId,
            homeworkId = homeworkId
        )
    }

    override suspend fun getHomeworkByGroupId(groupId: Int): Flow<List<Homework>> {
        return homeworkDao.getByGroupId(groupId).map {
            it.map { homework -> homework.toModel() }
        }
    }

    override suspend fun getAll(): Flow<List<Homework>> {
        return homeworkDao.getAll().map {
            it.map { homework -> homework.toModel() }
        }
    }

    override suspend fun getHomeworkById(homeworkId: Int): Flow<Homework?> {
        return homeworkDao.getById(homeworkId).map { it?.toModel() }
    }

    @Deprecated("Use split up methods instead instead")
    suspend fun insertHomework(
        id: Long?,
        profile: ClassProfile,
        defaultLessonVpId: Int?,
        storeInCloud: Boolean,
        shareWithClass: Boolean,
        until: ZonedDateTime,
        tasks: List<NewTaskRecord>,
        isHidden: Boolean,
        createdAt: ZonedDateTime,
        documentUris: List<Document>,
        onDocumentUploadProgressChanges: (Uri, Float) -> Unit
    ): HomeworkModificationResult {

        var homeworkId = id ?: (findLocalId() - 1L)
        val homeworkTasks = tasks.toMutableList()

        if (storeInCloud && profile.vppId != null) {
            // setup authentication
            val vppIdToken = vppIdRepository.getVppIdToken(profile.vppId) ?: return HomeworkModificationResult.FAILED
            vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)

            // post homework
            val result = vppIdNetworkRepository.doRequest(
                path = "/api/$API_VERSION/school/${profile.group.school.id}/group/${profile.group.groupId}/homework",
                requestBody = Gson().toJson(
                    AddHomeworkRequest(
                        vpId = defaultLessonVpId,
                        shareWithClass = shareWithClass,
                        until = ZonedDateTimeConverter().zonedDateTimeToTimestamp(until),
                        tasks = tasks.map { it.content }
                    )
                ),
                requestMethod = HttpMethod.Post
            )
            if (result.response != HttpStatusCode.Created || result.data == null) return HomeworkModificationResult.FAILED
            val response = ResponseDataWrapper.fromJson<AddHomeworkResponse>(result.data)

            homeworkId = response.id.toLong()
            response.tasks.forEach { responseTask ->
                homeworkTasks.replaceAll { task ->
                    if (task.content.sha256().lowercase() != responseTask.contentSHA256) return@replaceAll task
                    else task.copy(id = responseTask.id.toLong())
                }
            }
        }

        val dbHomework = DbHomework(
            id = homeworkId,
            groupId = profile.group.groupId,
            createdAt = createdAt,
            until = until,
            defaultLessonVpId = defaultLessonVpId,
            createdBy = if (storeInCloud) profile.vppId?.id else null,
            isPublic = shareWithClass,
            isHidden = isHidden,
            owningProfileId = if (storeInCloud) null else profile.id
        )
        homeworkDao.insert(dbHomework)
        homeworkTasks.forEach {
            val dbHomeworkTask = DbHomeworkTask(
                id = (it.id ?: (findLocalTaskId() - 1)).toInt(),
                homeworkId = dbHomework.id.toInt(),
                content = it.content,
                isDone = it.done
            )
            homeworkDao.insertTask(dbHomeworkTask)
        }

        documentUris.forEach document@{ document ->
            val inputStream = context.contentResolver.openInputStream(document.uri) ?: return@document
            val folder = File(context.filesDir, "homework_documents")
            folder.mkdirs()
            val binary = inputStream.readBytes()
            val documentId =
                if (storeInCloud) {
                    uploadDocument(profile, homeworkId, document.name + "." + document.extension, binary, onProgress = { bytesSentTotal, _ ->
                        onDocumentUploadProgressChanges(document.uri, (bytesSentTotal.toFloat() / binary.size).run { if (this.isNaN()) return@run 0f else this })
                    }) ?: return@document
                } else findLocalDocumentId() - 1
            val outputFile = File(folder, "$documentId.${document.extension}")
            val outputStream = FileOutputStream(outputFile)
            outputStream.write(binary)

            inputStream.close()
            outputStream.close()

            homeworkDocumentDao.upsertHomeworkDocument(DbHomeworkDocument(documentId, document.name, document.extension, homeworkId))
        }

        if (storeInCloud) return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        return HomeworkModificationResult.SUCCESS_OFFLINE
    }

    private suspend fun uploadDocument(
        profile: ClassProfile,
        homeworkId: Long,
        fileName: String,
        byteArray: ByteArray,
        onProgress: (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): Int? {
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${profile.group.school.id}/group/${profile.group.groupId}/homework/$homeworkId/document/",
            requestBody = byteArray,
            requestMethod = HttpMethod.Post,
            queries = mapOf("file_name" to fileName),
            onUploading = onProgress
        )
        if (response.response?.isSuccess() != true) return null
        val data = response.data ?: return null
        return ResponseDataWrapper.fromJson<UploadDocumentResponse>(data).id
    }

    override suspend fun changeDueDateCloud(vppId: VppId, homework: Homework, newDate: ZonedDateTime): Response<HomeworkModificationResult, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homework.id}",
            requestBody = Gson().toJson(
                ChangeDueToDateRequest(
                    until = ZonedDateTimeConverter().zonedDateTimeToTimestamp(newDate),
                )
            ),
            requestMethod = HttpMethod.Patch
        )
        if (result.response != HttpStatusCode.OK) return Response(HomeworkModificationResult.FAILED, null)
        homeworkDao.updateDueDate(homework.id, newDate)
        return Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, null)
    }

    override suspend fun changeDueDateDb(homework: Homework, newDate: ZonedDateTime) {
        homeworkDao.updateDueDate(homework.id, newDate)
    }

    override suspend fun findLocalId(): Int {
        val homework = homeworkDao.getAll().first().minByOrNull { it.homework.id }
        return minOf(homework?.homework?.id?.toInt() ?: 0, 0) - 1
    }

    override suspend fun findLocalTaskId(): Int {
        val task = homeworkDao.getAll().first().flatMap { it.tasks }.minByOrNull { it.id }
        return minOf(task?.id ?: 0, 0) - 1
    }

    override suspend fun findLocalDocumentId(): Int {
        return homeworkDocumentDao.getAllHomeworkDocuments().first().minByOrNull { it.id }?.id ?: 0
    }

    override suspend fun getHomeworkByTask(taskId: Int): Homework {
        val homeworkId = homeworkDao.getHomeworkTaskById(taskId).first().homeworkId
        return homeworkDao.getById(homeworkId).first()!!.toModel()
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

    override suspend fun addDocumentCloud(vppId: VppId, name: String, homeworkId: Int, type: HomeworkDocumentType, content: ByteArray, onUploading: (sent: Long, total: Long) -> Unit): Response<HomeworkModificationResult, Int?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/$homeworkId/document",
            requestBody = content,
            requestMethod = HttpMethod.Post,
            queries = mapOf("file_name" to name + "." + type.extension),
            onUploading = { sent, of -> onUploading(sent, of) }
        )
        if (response.response?.isSuccess() != true) return Response(HomeworkModificationResult.FAILED, null)
        val data = response.data ?: return Response(HomeworkModificationResult.FAILED, null)
        return Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, ResponseDataWrapper.fromJson<UploadDocumentResponse>(data).id)
    }

    override suspend fun addHomeworkDb(homeworkId: Int?, clazzProfile: ClassProfile?, defaultLessonVpId: Int?, dueTo: ZonedDateTime, vppId: VppId?, isHidden: Boolean, isPublic: Boolean, createdAt: ZonedDateTime): HomeworkId {
        val id = homeworkId ?: (findLocalId() - 1)
        homeworkDao.insert(DbHomework(
            id = id.toLong(),
            groupId = if (vppId == null) clazzProfile!!.group.groupId else vppId.group!!.groupId,
            defaultLessonVpId = defaultLessonVpId,
            until = dueTo,
            isHidden = isHidden,
            isPublic = isPublic,
            owningProfileId = if (vppId == null) clazzProfile!!.id else null,
            createdBy = vppId?.id,
            createdAt = createdAt
        ))
        return id
    }

    override suspend fun addHomeworkCloud(vppId: VppId, dueTo: ZonedDateTime, tasks: List<String>, vpId: Int?, isPublic: Boolean): Response<HomeworkModificationResult, AddHomeworkResponse?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
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
        if (response.response?.isSuccess() != true || response.data == null) return Response(HomeworkModificationResult.FAILED, null)
        return Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, ResponseDataWrapper.fromJson<AddHomeworkResponse>(response.data))
    }

    override suspend fun addTaskDb(homeworkId: Int, taskId: Int?, isDone: Boolean, content: String): HomeworkTaskId {
        val id = taskId ?: (findLocalTaskId() - 1)
        homeworkDao.insertTask(DbHomeworkTask(
            id = id,
            homeworkId = homeworkId,
            content = content,
            isDone = isDone
        ))
        return id
    }

    override suspend fun addTaskCloud(vppId: VppId, homeworkId: Int, content: String): Response<HomeworkModificationResult, Int?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/$homeworkId/task",
            requestBody = Gson().toJson(AddOrChangeTaskRequest(content)),
            requestMethod = HttpMethod.Post
        )
        if (response.response?.isSuccess() != true || response.data == null) return Response(HomeworkModificationResult.FAILED, null)
        return Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, ResponseDataWrapper.fromJson<AddTaskResponse>(response.data).id.toInt())
    }

    override suspend fun changeTaskStateCloud(vppId: VppId, homeworkTaskId: Int, isDone: Boolean): Response<HomeworkModificationResult, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        val homework = getHomeworkByTask(homeworkTaskId)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homework.id}/task/$homeworkTaskId",
            requestBody = Gson().toJson(MarkDoneRequest(isDone)),
            requestMethod = HttpMethod.Patch
        )
        return if (response.response?.isSuccess() == true) Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, Unit)
        else Response(HomeworkModificationResult.FAILED, null)
    }

    override suspend fun changeTaskStateDb(homeworkTaskId: Int, isDone: Boolean) {
        val task = homeworkDao.getHomeworkTaskById(homeworkTaskId).first()
        homeworkDao.insertTask(task.copy(isDone = isDone))
    }

    override suspend fun changeDocumentNameCloud(vppId: VppId, homeworkDocument: es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument, newName: String): Response<HomeworkModificationResult, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homeworkDocument.homeworkId}/document/${homeworkDocument.documentId}/name",
            requestBody = Gson().toJson(RenameDocumentRequest(newName)),
            requestMethod = HttpMethod.Patch
        )
        return if (response.response?.isSuccess() == true) Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, Unit)
        else Response(HomeworkModificationResult.FAILED, null)
    }

    override suspend fun changeDocumentNameDb(homeworkDocument: es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument, newName: String) {
        homeworkDocumentDao.updateHomeworkDocumentFileName(homeworkDocument.documentId, newName)
    }

    override suspend fun deleteDocumentCloud(vppId: VppId, homeworkDocument: es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument): Response<HomeworkModificationResult, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homeworkDocument.homeworkId}/document/${homeworkDocument.documentId}",
            requestMethod = HttpMethod.Delete
        )
        return if (response.response?.isSuccess() == true) Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, Unit)
        else Response(HomeworkModificationResult.FAILED, null)
    }

    override suspend fun deleteDocumentDb(homeworkDocument: es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument) {
        homeworkDocumentDao.deleteHomeworkDocumentById(homeworkDocument.documentId)
    }

    override suspend fun changeHomeworkSharingDb(homework: CloudHomework, isPublic: Boolean) {
        homeworkDao.changePublic(homework.id, isPublic)
    }

    override suspend fun changeHomeworkSharingCloud(vppId: VppId, homework: CloudHomework, isPublic: Boolean): Response<HomeworkModificationResult, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homework.id}",
            requestBody = Gson().toJson(ChangeVisibilityRequest(isPublic)),
            requestMethod = HttpMethod.Patch
        )
        return if (response.response?.isSuccess() == true) Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, Unit)
        else Response(HomeworkModificationResult.FAILED, null)
    }

    override suspend fun changeHomeworkVisibilityDb(homework: CloudHomework, hide: Boolean) {
        homeworkDao.changeHidden(homework.id, hide)
    }

    override suspend fun deleteHomeworkDb(homework: Homework) {
        homeworkDao.deleteHomework(homework.id)
    }

    override suspend fun deleteHomeworkCloud(vppId: VppId, homework: CloudHomework): Response<HomeworkModificationResult, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homework.id}",
            requestMethod = HttpMethod.Delete
        )
        return if (response.response?.isSuccess() == true) Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, Unit)
        else Response(HomeworkModificationResult.FAILED, null)
    }

    override suspend fun changeTaskContentCloud(vppId: VppId, homeworkTask: HomeworkTask, newContent: String): Response<HomeworkModificationResult, Unit?> {
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homeworkTask.homeworkId}/task/${homeworkTask.id}",
            requestBody = Gson().toJson(AddOrChangeTaskRequest(newContent)),
            requestMethod = HttpMethod.Patch
        )
        return if (response.response?.isSuccess() == true) Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, Unit)
        else Response(HomeworkModificationResult.FAILED, null)
    }

    override suspend fun changeTaskContentDb(homeworkTask: HomeworkTask, newContent: String) {
        homeworkDao.insertTask(
            DbHomeworkTask(
            id = homeworkTask.id,
            homeworkId = homeworkTask.homeworkId,
            content = newContent,
            isDone = homeworkTask.isDone
        ))
    }

    override suspend fun deleteTaskCloud(vppId: VppId, task: HomeworkTask): Response<HomeworkModificationResult, Unit?> {
        val homework = getHomeworkByTask(task.id)
        val token = vppIdRepository.getVppIdToken(vppId) ?: return Response(HomeworkModificationResult.FAILED, null)
        if (vppId.group?.school == null) return Response(HomeworkModificationResult.FAILED, null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${vppId.group.school.id}/group/${vppId.group.groupId}/homework/${homework.id}/task/${task.id}",
            requestMethod = HttpMethod.Delete
        )
        return if (response.response?.isSuccess() == true) Response(HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE, Unit)
        else Response(HomeworkModificationResult.FAILED, null)
    }

    override suspend fun deleteTaskDb(task: HomeworkTask) {
        homeworkDao.deleteTask(task.id)
    }

    override suspend fun getDocumentById(id: Int): es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument? {
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
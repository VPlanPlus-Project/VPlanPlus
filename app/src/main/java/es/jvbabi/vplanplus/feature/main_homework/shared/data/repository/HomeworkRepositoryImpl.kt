package es.jvbabi.vplanplus.feature.main_homework.shared.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDocumentDao
import es.jvbabi.vplanplus.data.source.database.dao.PreferredHomeworkNotificationTimeDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_DEFAULT_NOTIFICATION_ID_HOMEWORK
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_HOMEWORK
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.data.model.DbPreferredNotificationTime
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.DeleteTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.NewTaskRecord
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.getRelativeStringResource
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
import java.time.format.DateTimeFormatter

class HomeworkRepositoryImpl(
    private val homeworkDao: HomeworkDao,
    private val homeworkDocumentDao: HomeworkDocumentDao,
    private val homeworkNotificationTimeDao: PreferredHomeworkNotificationTimeDao,
    private val vppIdRepository: VppIdRepository,
    private val profileRepository: ProfileRepository,
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val keyValueRepository: KeyValueRepository,
    private val context: Context
) : HomeworkRepository {

    private var isUpdateRunning = false

    override suspend fun fetchHomework(sendNotification: Boolean) {
        if (isUpdateRunning) return
        isUpdateRunning = true
        keyValueRepository.set(Keys.IS_HOMEWORK_UPDATE_RUNNING, "true")
        profileRepository
            .getProfiles()
            .first()
            .filterIsInstance<ClassProfile>()
            .filter { it.isHomeworkEnabled }
            .forEach { profile ->
                val url = if (profile.vppId != null) {
                    val token = vppIdRepository.getVppIdToken(profile.vppId) ?: return@forEach
                    vppIdNetworkRepository.authentication = BearerAuthentication(token)
                    "/api/$API_VERSION/user/me/homework"
                } else {
                    vppIdNetworkRepository.authentication = profile.group.school.buildAccess().buildVppAuthentication()
                    "/api/$API_VERSION/school/${profile.group.school.id}/class/${profile.group.name}/homework"
                }

                val response = vppIdNetworkRepository.doRequest(url)

                if (response.response != HttpStatusCode.OK || response.data == null) return@forEach
                val data = Gson().fromJson(response.data, HomeworkResponse::class.java).homework

                homeworkDao
                    .getAll()
                    .first()
                    .filter { data.none { nd -> nd.id == it.homework.id } }
                    .filter { it.homework.id > 0 }
                    .filter { it.classes.group.name == profile.group.name }
                    .map { it.homework.id }
                    .forEach { homeworkDao.deleteHomework(it) }

                val existingHomework = getAll().first().filter { it.group == profile.group }
                val newHomework = data
                    .filter { profile.isDefaultLessonEnabled(it.vpId) }
                    .filter { !existingHomework.any { eh -> eh.id == it.id } }
                    .filter { it.createdBy != profile.vppId?.id?.toLong() }

                data.forEach forEachHomework@{ responseHomework ->
                    val isNewHomework = existingHomework.none { it.id == responseHomework.id }
                    val id = responseHomework.id
                    val createdBy = vppIdRepository.getVppId(responseHomework.createdBy, profile.getSchool(), false)
                    val until = ZonedDateTimeConverter().timestampToZonedDateTime(responseHomework.until)

                    val existingRecord = homeworkDao
                        .getById(responseHomework.id.toInt()).first()
                        ?.toModel(context)
                    homeworkDao.deleteTasksForHomework(responseHomework.id)

                    homeworkDao.insert(
                        DbHomework(
                            id = id,
                            groupId = profile.group.groupId,
                            createdAt = ZonedDateTimeConverter().timestampToZonedDateTime(responseHomework.createdAt),
                            until = until,
                            defaultLessonVpId = responseHomework.vpId,
                            createdBy = createdBy?.id,
                            isPublic = responseHomework.shareWithClass,
                            isHidden = (existingRecord?.isHidden ?: (isNewHomework && until.isBefore(ZonedDateTime.now())) && createdBy?.id != profile.vppId?.id),
                            profileId = profile.id
                        )
                    )
                    responseHomework.tasks.forEach { task ->
                        homeworkDao.insertTask(
                            DbHomeworkTask(
                                id = task.id.toLong(),
                                homeworkId = id,
                                content = task.content,
                                isDone = task.done ?: until.isBefore(ZonedDateTime.now())
                            )
                        )
                    }

                    responseHomework.documentIds.forEach document@{ documentId ->
                        val file = File(context.filesDir, "homework_documents/$documentId.pdf")
                        if (file.exists()) return@document
                        val binary = getHomeworkDocument(profile.group.name, id, documentId, profile.vppId?.id?.toLong() != responseHomework.createdBy) ?: return@document
                        val outputStream = FileOutputStream(file)
                        outputStream.write(binary)
                        outputStream.close()
                    }
                }

                if (sendNotification) {
                    val showNewNotification = keyValueRepository.getOrDefault(
                        Keys.SHOW_NOTIFICATION_ON_NEW_HOMEWORK,
                        Keys.SHOW_NOTIFICATION_ON_NEW_HOMEWORK_DEFAULT
                    ).toBoolean()
                    val pendingIntent = Intent(context, MainActivity::class.java)
                        .putExtra("screen", "homework")
                        .let { intent ->
                            PendingIntent.getActivity(
                                context,
                                0,
                                intent,
                                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }

                    val notificationRelevantNewHomework = newHomework.filter { ZonedDateTimeConverter().timestampToZonedDateTime(it.until).isAfter(ZonedDateTime.now()) }

                    if (notificationRelevantNewHomework.size == 1 && showNewNotification) {
                        val defaultLessons =
                            defaultLessonRepository.getDefaultLessonByGroupId(profile.group.groupId)

                        val dateResourceId = DateUtils
                            .getDateFromTimestamp(notificationRelevantNewHomework.first().until)
                            .getRelativeStringResource()

                        val dateString =
                            if (dateResourceId != null) stringRepository.getString(dateResourceId)
                            else DateUtils.getDateFromTimestamp(notificationRelevantNewHomework.first().until).format(
                                DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")
                            )

                        notificationRepository.sendNotification(
                            CHANNEL_ID_HOMEWORK,
                            notificationRelevantNewHomework.first().id.toInt(),
                            stringRepository.getString(R.string.notification_homeworkNewHomeworkOneTitle),
                            stringRepository.getString(
                                R.string.notification_homeworkNewHomeworkOneContent,
                                vppIdRepository.getVppId(notificationRelevantNewHomework.first().createdBy, profile.group.school, false)?.name
                                    ?: "Unknown",
                                defaultLessons.firstOrNull { it.vpId == notificationRelevantNewHomework.first().vpId }?.subject
                                    ?: "Unknown",
                                notificationRelevantNewHomework.first().tasks.size,
                                dateString
                            ),
                            R.drawable.vpp,
                            pendingIntent,
                        )
                    } else if (notificationRelevantNewHomework.isNotEmpty() && showNewNotification) {
                        notificationRepository.sendNotification(
                            CHANNEL_ID_HOMEWORK,
                            CHANNEL_DEFAULT_NOTIFICATION_ID_HOMEWORK,
                            stringRepository.getString(R.string.notification_homeworkNewHomeworkMultipleTitle),
                            stringRepository.getString(
                                R.string.notification_homeworkNewHomeworkMultipleContent,
                                notificationRelevantNewHomework.size
                            ),
                            R.drawable.vpp,
                            pendingIntent,
                        )
                    }
                }
            }
        keyValueRepository.set(Keys.IS_HOMEWORK_UPDATE_RUNNING, "false")
        isUpdateRunning = false
    }

    private suspend fun getHomeworkDocument(className: String, homeworkId: Long, documentId: Int, usePublicAuth: Boolean): ByteArray? {
        if (usePublicAuth) {
            val response = vppIdNetworkRepository.doRequestRaw(
                path = "/api/$API_VERSION/school/$className/homework/$homeworkId/document/$documentId",
                requestMethod = HttpMethod.Get
            )
            return response.data
        }
        return vppIdNetworkRepository.doRequestRaw(
            path = "/api/$API_VERSION/user/me/homework/$homeworkId/document/$documentId",
            requestMethod = HttpMethod.Get
        ).data
    }

    override suspend fun getHomeworkByGroupId(groupId: Int): Flow<List<Homework>> {
        return homeworkDao.getByGroupId(groupId).map {
            it.map { homework -> homework.toModel(context) }
        }
    }

    override suspend fun getAll(): Flow<List<Homework>> {
        return homeworkDao.getAll().map {
            it.map { homework -> homework.toModel(context) }
        }
    }

    override suspend fun getHomeworkById(homeworkId: Int): Flow<Homework?> {
        return homeworkDao.getById(homeworkId).map { it?.toModel(context) }
    }

    override suspend fun insertHomework(
        id: Long?,
        profile: ClassProfile,
        group: Group,
        defaultLessonVpId: Int?,
        storeInCloud: Boolean,
        shareWithClass: Boolean,
        until: ZonedDateTime,
        tasks: List<NewTaskRecord>,
        isHidden: Boolean,
        createdAt: ZonedDateTime,
        documentUris: List<Uri>
    ): HomeworkModificationResult {

        var homeworkId = id ?: (findLocalId() - 1L)
        val homeworkTasks = tasks.toMutableList()

        if (storeInCloud && profile.vppId != null) {
            // setup authentication
            val vppIdToken = vppIdRepository.getVppIdToken(profile.vppId) ?: return HomeworkModificationResult.FAILED
            vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)

            // post homework
            val result = vppIdNetworkRepository.doRequest(
                path = "/api/$API_VERSION/user/me/homework",
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
            val response = Gson().fromJson(result.data, AddHomeworkResponse::class.java)

            homeworkId = response.id
            response.tasks.forEach { responseTask ->
                homeworkTasks.replaceAll { task ->
                    if (task.content != responseTask.content) return@replaceAll task
                    else task.copy(id = responseTask.id)
                }
            }
        }

        val dbHomework = DbHomework(
            id = homeworkId,
            groupId = group.groupId,
            createdAt = createdAt,
            until = until,
            defaultLessonVpId = defaultLessonVpId,
            createdBy = if (storeInCloud) profile.vppId?.id else null,
            isPublic = shareWithClass,
            isHidden = isHidden,
            profileId = profile.id
        )
        homeworkDao.insert(dbHomework)
        homeworkTasks.forEach {
            val dbHomeworkTask = DbHomeworkTask(
                id = it.id ?: (findLocalTaskId() - 1),
                homeworkId = dbHomework.id,
                content = it.content,
                isDone = it.done
            )
            homeworkDao.insertTask(dbHomeworkTask)
        }

        documentUris.forEach document@{ uri ->
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@document
            val folder = File(context.filesDir, "homework_documents")
            folder.mkdirs()
            val binary = inputStream.readBytes()
            val documentId =
                if (storeInCloud) {
                    uploadDocument(homeworkId, binary) ?: return@document
                } else findLocalDocumentId() - 1
            val outputFile = File(folder, "$documentId.pdf")
            val outputStream = FileOutputStream(outputFile)
            outputStream.write(binary)

            inputStream.close()
            outputStream.close()

//            homeworkDocumentDao.insertHomeworkDocument(DbHomeworkDocument(documentId, homeworkId)) TODO
        }

        if (storeInCloud) return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        return HomeworkModificationResult.SUCCESS_OFFLINE
    }

    private suspend fun uploadDocument(
        homeworkId: Long,
        byteArray: ByteArray
    ): Int? {
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me/homework/$homeworkId/document",
            requestBody = byteArray,
            requestMethod = HttpMethod.Post
        )
        if (response.response?.isSuccess() != true) return null
        val data = response.data ?: return null
        return Gson().fromJson(data, UploadDocumentResponse::class.java).id
    }

    override suspend fun removeOrHideHomework(
        homework: Homework,
        task: DeleteTask
    ): HomeworkModificationResult {

        return when (task) {
            DeleteTask.HIDE -> {
                homeworkDao.changeHidden(homework.id, true)
                HomeworkModificationResult.SUCCESS_OFFLINE
            }

            DeleteTask.DELETE -> {
                if (homework.id > 0) {
                    val vppId = vppIdRepository
                        .getVppIds().first()
                        .firstOrNull { it.isActive() && it.id == homework.createdBy?.id }
                        ?: return HomeworkModificationResult.FAILED
                    val vppIdToken =
                        vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
                    vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
                    val result = vppIdNetworkRepository.doRequest(
                        path = "/api/$API_VERSION/user/me/homework/${homework.id}",
                        requestMethod = HttpMethod.Delete
                    )
                    if (result.response != HttpStatusCode.OK) return HomeworkModificationResult.FAILED
                }

                deleteDocumentsByHomeworkId(homework.id)
                homeworkDao.deleteHomework(homework.id)
                if (homework.id > 0) HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE else HomeworkModificationResult.SUCCESS_OFFLINE
            }

            DeleteTask.FORCE_DELETE_LOCALLY -> {
                deleteDocumentsByHomeworkId(homework.id)
                homeworkDao.deleteHomework(homework.id)
                HomeworkModificationResult.SUCCESS_OFFLINE
            }
        }
    }

    private suspend fun deleteDocumentsByHomeworkId(homeworkId: Long) {
        homeworkDocumentDao.getHomeworkDocumentsByHomeworkId(homeworkId).first().map {
            File(context.filesDir, "homework_documents/${it.id}.pdf").delete()
            homeworkDocumentDao.deleteHomeworkDocumentById(it.id)
        }
    }

    override suspend fun addNewTask(
        homework: Homework,
        content: String
    ): HomeworkModificationResult {
        if (homework.id < 0) {
            val dbHomeworkTask = DbHomeworkTask(
                id = findLocalTaskId() - 1,
                homeworkId = homework.id,
                content = content,
                isDone = false
            )
            homeworkDao.insertTask(dbHomeworkTask)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppId = vppIdRepository.getVppId(homework.createdBy!!.id.toLong(), homework.group.school, false) ?: return HomeworkModificationResult.FAILED

        val vppIdToken =
            vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me/homework/${homework.id}/tasks",
            requestBody = Gson().toJson(AddOrChangeTaskRequest(content = content)),
            requestMethod = HttpMethod.Post
        )

        if (result.response != HttpStatusCode.Created || result.data == null) return HomeworkModificationResult.FAILED
        val response = Gson().fromJson(result.data, AddHomeworkResponseTask::class.java)
        val dbHomeworkTask = DbHomeworkTask(
            id = response.id,
            homeworkId = homework.id,
            content = response.content,
            isDone = false
        )
        homeworkDao.insertTask(dbHomeworkTask)
        return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
    }

    override suspend fun deleteTask(task: HomeworkTask): HomeworkModificationResult {
        val parent = getHomeworkByTask(task)

        if (task.id < 0) {
            homeworkDao.deleteTask(task.id)
            if (getHomeworkById(parent.id.toInt()).first()?.tasks?.isEmpty() == true) {
                homeworkDao.deleteHomework(parent.id)
            }
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == parent.createdBy?.id }
            ?: return HomeworkModificationResult.FAILED

        val vppIdToken =
            vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me/homework/${parent.id}/tasks/${task.id}",
            requestMethod = HttpMethod.Delete
        )

        return if (result.response == HttpStatusCode.OK || result.response == HttpStatusCode.NotFound) {
            homeworkDao.deleteTask(task.id)
            if (getHomeworkById(parent.id.toInt()).first()?.tasks?.isEmpty() == true) {
                homeworkDao.deleteHomework(parent.id)
            }
            HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
    }

    override suspend fun editTaskContent(
        task: HomeworkTask,
        newContent: String
    ): HomeworkModificationResult {
        if (task.id < 0) {
            val dbHomeworkTask =
                homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(content = newContent)
            homeworkDao.insertTask(dbHomeworkTask)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val parent = getHomeworkByTask(task)
        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == parent.createdBy?.id }
            ?: return HomeworkModificationResult.FAILED

        val vppIdToken =
            vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me/homework/${parent.id}/tasks/${task.id}",
            requestBody = Gson().toJson(AddOrChangeTaskRequest(newContent)),
            requestMethod = HttpMethod.Patch
        )

        return if (result.response == HttpStatusCode.OK) {
            val dbHomeworkTask =
                homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(content = newContent)
            homeworkDao.insertTask(dbHomeworkTask)
            HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
    }

    override suspend fun setTaskState(
        homework: Homework,
        task: HomeworkTask,
        done: Boolean
    ): HomeworkModificationResult {
        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.group == homework.group } // fixme: Use vpp.ID provided in parameter; this creates problems when multiple vpp.IDs are connected to the same class

        if (task.id < 0 || vppId == null) {
            val dbHomeworkTask =
                homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(isDone = done)
            homeworkDao.insertTask(dbHomeworkTask)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppIdToken =
            vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me/homework/${homework.id}/tasks/${task.id}",
            requestBody = Gson().toJson(MarkDoneRequest(done)),
            requestMethod = HttpMethod.Patch
        )
        return if (result.response == HttpStatusCode.OK) {
            val dbHomeworkTask =
                homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(isDone = done)
            homeworkDao.insertTask(dbHomeworkTask)
            HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
    }

    override suspend fun findLocalId(): Long {
        val homework = homeworkDao.getAll().first().minByOrNull { it.homework.id }
        return minOf(homework?.homework?.id ?: 0, 0) - 1
    }

    override suspend fun findLocalTaskId(): Long {
        val task = homeworkDao.getAll().first().flatMap { it.tasks }.minByOrNull { it.id }
        return minOf(task?.id ?: 0, 0) - 1
    }

    override suspend fun findLocalDocumentId(): Int {
        return homeworkDocumentDao.getAllHomeworkDocuments().first().minByOrNull { it.id }?.id ?: 0
    }

    override suspend fun getHomeworkByTask(task: HomeworkTask): Homework {
        val homeworkId = homeworkDao.getHomeworkTaskById(task.id.toInt()).first().homeworkId
        return homeworkDao.getById(homeworkId.toInt()).first()!!.toModel(context)
    }

    override suspend fun changeShareStatus(homework: Homework): HomeworkModificationResult {
        if (homework.id < 0) throw UnsupportedOperationException("Cannot change visibility of local homework")
        val vppId = homework.createdBy
            ?: throw UnsupportedOperationException("Cannot change visibility of homework without creator")

        val vppIdToken =
            vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me/homework/${homework.id}",
            requestBody = Gson().toJson(
                ChangeVisibilityRequest(shared = !homework.isPublic)
            ),
            requestMethod = HttpMethod.Patch
        )

        return if (result.response == HttpStatusCode.OK) {
            homeworkDao.changePublic(homework.id, !homework.isPublic)
            HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
    }

    override suspend fun updateDueDate(
        homework: Homework,
        newDate: ZonedDateTime
    ): HomeworkModificationResult {
        if (homework.id < 0) {
            homeworkDao.updateDueDate(homework.id, newDate)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }
        val vppId = homework.createdBy
            ?: throw UnsupportedOperationException("Cannot change due date of homework without creator")
        val vppIdToken =
            vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me/homework/${homework.id}",
            requestBody = Gson().toJson(
                ChangeDueToDateRequest(
                    until = ZonedDateTimeConverter().zonedDateTimeToTimestamp(newDate),
                )
            ),
            requestMethod = HttpMethod.Patch
        )
        if (result.response != HttpStatusCode.OK) return HomeworkModificationResult.FAILED
        homeworkDao.updateDueDate(homework.id, newDate)
        return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
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
}


private data class HomeworkResponse(
    @SerializedName("data") val homework: List<HomeworkResponseRecord>
)

private data class UploadDocumentResponse(
    @SerializedName("data") val id: Int
)

private data class HomeworkResponseRecord(
    @SerializedName("id") val id: Long,
    @SerializedName("vp_id") val vpId: Int,
    @SerializedName("due_to") val until: Long,
    @SerializedName("created_by") val createdBy: Long,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("public") val shareWithClass: Boolean,
    @SerializedName("tasks") val tasks: List<HomeRecordTask>,
    @SerializedName("documentIds") val documentIds: List<Int>
)

private data class HomeRecordTask @JvmOverloads constructor(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val content: String,
    @SerializedName("done") val done: Boolean? = null
)

private data class MarkDoneRequest(
    @SerializedName("done") val done: Boolean
)

private data class AddOrChangeTaskRequest(
    @SerializedName("task") val content: String
)

private data class AddHomeworkRequest(
    @SerializedName("vp_id") val vpId: Int?,
    @SerializedName("public") val shareWithClass: Boolean,
    @SerializedName("due_to") val until: Long,
    @SerializedName("tasks") val tasks: List<String>
)

private data class AddHomeworkResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("tasks") val tasks: List<AddHomeworkResponseTask>,
)

private data class AddHomeworkResponseTask(
    @SerializedName("id") val id: Long,
    @SerializedName("content") val content: String
)

private data class ChangeVisibilityRequest(
    @SerializedName("public") var shared: Boolean,
)

private data class ChangeDueToDateRequest(
    @SerializedName("due_to") val until: Long
)
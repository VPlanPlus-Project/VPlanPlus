package es.jvbabi.vplanplus.feature.main_homework.shared.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.repository.ResponseDataWrapper
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDocumentDao
import es.jvbabi.vplanplus.data.source.database.dao.PreferredHomeworkNotificationTimeDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
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
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.LocalHomework
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
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.getRelativeStringResource
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

    @Deprecated("don't do this")
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
                if (profile.vppId != null) {
                    val token = vppIdRepository.getVppIdToken(profile.vppId) ?: return@forEach
                    vppIdNetworkRepository.authentication = BearerAuthentication(token)
                } else vppIdNetworkRepository.authentication = profile.group.school.buildAccess().buildVppAuthentication()

                val response = vppIdNetworkRepository.doRequest("/api/$API_VERSION/school/${profile.group.school.id}/group/${profile.group.groupId}/homework")

                if (response.response != HttpStatusCode.OK || response.data == null) {
                    Log.w("HomeworkRepository.fetchHomework", "Failed to fetch homework for ${profile.displayName} (${profile.id}): Response code ${response.response?.value}")
                    return@forEach
                }
                val data = ResponseDataWrapper.fromJson<List<HomeworkResponse>>(response.data)

                Log.d("HomeworkRepository.fetchHomework", "Response for ${profile.displayName} (${profile.id}) contains ${data.size} homework")

                homeworkDao
                    .getAll()
                    .first()
                    .filter { data.none { nd -> nd.id == it.homework.id } } // homework that isn't present in the response anymore
                    .filter { it.homework.id > 0 } // only delete non-local homework
                    .filter { it.classes.group.id == profile.group.groupId } // only delete homework for the current group
                    .map { it.homework.id }
                    .forEach { homeworkDao.deleteHomework(it) }

                val existingHomework = getAll().first().filter { it.group == profile.group }
                val newHomework = data
                    .filter { profile.isDefaultLessonEnabled(it.vpId) }
                    .filter { !existingHomework.any { eh -> eh.id == it.id } }
                    .filter { it.createdBy != profile.vppId?.id?.toLong() }

                data.forEach forEachHomework@{ responseHomework ->
                    val isNewHomework = existingHomework.none { it.id == responseHomework.id }
                    val homeworkId = responseHomework.id
                    val createdBy = vppIdRepository.getVppId(responseHomework.createdBy, profile.getSchool(), false)
                    val until = ZonedDateTimeConverter().timestampToZonedDateTime(responseHomework.until)

                    val existingRecord = homeworkDao
                        .getById(responseHomework.id.toInt()).first()
                        ?.toModel(context)
                    homeworkDao.deleteTasksForHomework(responseHomework.id)

                    homeworkDao.insert(
                        DbHomework(
                            id = homeworkId,
                            groupId = profile.group.groupId,
                            createdAt = ZonedDateTimeConverter().timestampToZonedDateTime(responseHomework.createdAt),
                            until = until,
                            defaultLessonVpId = responseHomework.vpId,
                            createdBy = createdBy?.id,
                            isPublic = responseHomework.shareWithClass,
                            isHidden = ((existingRecord as? CloudHomework)?.isHidden ?: (isNewHomework && until.isBefore(ZonedDateTime.now())) && createdBy?.id != profile.vppId?.id),
                            owningProfileId = null
                        )
                    )
                    responseHomework.tasks.forEach { task ->
                        homeworkDao.insertTask(
                            DbHomeworkTask(
                                id = task.id.toLong(),
                                homeworkId = homeworkId,
                                content = task.content,
                                isDone = task.done ?: until.isBefore(ZonedDateTime.now())
                            )
                        )
                    }

                    val documentFolder = File(context.filesDir, "homework_documents")
                    if (!documentFolder.exists()) documentFolder.mkdirs()
                    responseHomework.documentIds.forEach document@{ documentId ->
                        val file = File(documentFolder, documentId.toString())
                        if (file.exists() && homeworkDocumentDao.getHomeworkDocumentById(documentId) != null) return@document
                        val document = getHomeworkDocument(profile.group.school.id, profile.group.groupId, homeworkId, documentId) ?: return@document
                        val outputStream = FileOutputStream(file)
                        outputStream.write(document.content)
                        outputStream.close()
                        homeworkDocumentDao.insertHomeworkDocument(DbHomeworkDocument(documentId, document.name, document.extension, homeworkId))
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

    private suspend fun getHomeworkDocument(schoolId: Int, groupId: Int, homeworkId: Long, documentId: Int): HomeworkDocument? {
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/$schoolId/group/$groupId/homework/$homeworkId/document/$documentId",
            requestMethod = HttpMethod.Get
        )
        if (response.response?.isSuccess() != true || response.data == null) return null
        val data = ResponseDataWrapper.fromJson<HomeworkDocumentResponse>(response.data)
        try {
            HomeworkDocumentType.fromExtension(data.extension)
        } catch (e: IllegalArgumentException) {
            Log.e("HomeworkRepository.getHomeworkDocument", "Unknown or unsupported extension: ${data.extension}")
            return null
        }
        val contentResponse = vppIdNetworkRepository.doRequestRaw(
            path = "/api/$API_VERSION/school/$schoolId/group/$groupId/homework/$homeworkId/document/$documentId/content",
            requestMethod = HttpMethod.Get
        )
        if (contentResponse.response?.isSuccess() != true || contentResponse.data == null) return null
        return HomeworkDocument(
            content = contentResponse.data,
            name = data.name,
            extension = data.extension,
            id = documentId
        )
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

    @Deprecated("Use split up methods instead instead")
    override suspend fun insertHomework(
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
                id = it.id ?: (findLocalTaskId() - 1),
                homeworkId = dbHomework.id,
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
            val outputFile = File(folder, "$documentId")
            val outputStream = FileOutputStream(outputFile)
            outputStream.write(binary)

            inputStream.close()
            outputStream.close()

            homeworkDocumentDao.insertHomeworkDocument(DbHomeworkDocument(documentId, document.name, document.extension, homeworkId))
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

    @Deprecated("Use split up methods instead instead")
    override suspend fun deleteTask(
        profile: ClassProfile,
        task: HomeworkTask
    ): HomeworkModificationResult {
        val parent = getHomeworkByTask(task.id.toInt())

        if (task.id < 0) {
            homeworkDao.deleteTask(task.id)
            if (getHomeworkById(parent.id.toInt()).first()?.tasks?.isEmpty() == true) {
                homeworkDao.deleteHomework(parent.id)
            }
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }
        (parent as? CloudHomework) ?: throw IllegalStateException("A homework or task with an id greater than 0 should always be a cloud homework or task")

        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == parent.createdBy.id }
            ?: return HomeworkModificationResult.FAILED

        val vppIdToken =
            vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${profile.group.school.id}/group/${profile.group.groupId}/homework/${parent.id}/task/${task.id}/",
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

    @Deprecated("Use split up methods instead instead")
    override suspend fun editTaskContent(
        profile: ClassProfile,
        task: HomeworkTask,
        newContent: String
    ): HomeworkModificationResult {
        if (task.id < 0) {
            val dbHomeworkTask =
                homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(content = newContent)
            homeworkDao.insertTask(dbHomeworkTask)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val parent = getHomeworkByTask(task.id.toInt()) as? CloudHomework
            ?: throw IllegalStateException("A homework or task with an id greater than 0 should always be a cloud homework or task")
        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == parent.createdBy.id }
            ?: return HomeworkModificationResult.FAILED

        val vppIdToken =
            vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${profile.group.school.id}/group/${profile.group.groupId}/homework/${parent.id}/task/${task.id}/",
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

    @Deprecated("Use split up methods instead instead")
    override suspend fun setTaskState(
        profile: ClassProfile,
        homework: Homework,
        task: HomeworkTask,
        done: Boolean
    ): HomeworkModificationResult {
        val vppId = profile.vppId

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
            path = "/api/$API_VERSION/school/${profile.group.school.id}/group/${profile.group.groupId}/homework/${homework.id}/task/${task.id}/",
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

    override suspend fun getHomeworkByTask(taskId: Int): Homework {
        val homeworkId = homeworkDao.getHomeworkTaskById(taskId).first().homeworkId
        return homeworkDao.getById(homeworkId.toInt()).first()!!.toModel(context)
    }

    @Deprecated("Use split up methods instead instead")
    override suspend fun updateDueDate(
        profile: ClassProfile,
        homework: Homework,
        newDate: ZonedDateTime
    ): HomeworkModificationResult {
        if (homework is LocalHomework) {
            homeworkDao.updateDueDate(homework.id, newDate)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }
        (homework as? CloudHomework) ?: throw IllegalStateException("A homework with an id greater than 0 should always be a cloud homework")
        val vppId = homework.createdBy
        val vppIdToken = vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/school/${profile.group.school.id}/group/${profile.group.groupId}/homework/${homework.id}/",
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

    override suspend fun addDocumentToDb(documentId: Int?, homeworkId: Int, name: String, type: HomeworkDocumentType): HomeworkDocumentId {
        val id = documentId ?: (findLocalDocumentId() - 1)
        homeworkDocumentDao.insertHomeworkDocument(
            DbHomeworkDocument(
                id,
                fileName = name,
                homeworkId = homeworkId.toLong(),
                fileType = type.extension
            )
        )
        return id
    }

    override suspend fun uploadDocument(vppId: VppId, name: String, homeworkId: Int, type: HomeworkDocumentType, content: ByteArray, onUploading: (sent: Long, total: Long) -> Unit): Response<HomeworkModificationResult, Int?> {
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

    override suspend fun addHomeworkToDb(homeworkId: Int?, clazzProfile: ClassProfile, defaultLessonVpId: Int?, dueTo: ZonedDateTime, vppId: VppId?, isHidden: Boolean, isPublic: Boolean, createdAt: ZonedDateTime): HomeworkId {
        val id = homeworkId ?: (findLocalId().toInt() - 1)
        homeworkDao.insert(DbHomework(
            id = id.toLong(),
            groupId = clazzProfile.group.groupId,
            defaultLessonVpId = defaultLessonVpId,
            until = dueTo,
            isHidden = isHidden,
            isPublic = isPublic,
            owningProfileId = if (vppId == null) clazzProfile.id else null,
            createdBy = vppId?.id,
            createdAt = createdAt
        ))
        return id
    }

    override suspend fun uploadHomework(vppId: VppId, dueTo: ZonedDateTime, tasks: List<String>, vpId: Int?, isPublic: Boolean): Response<HomeworkModificationResult, AddHomeworkResponse?> {
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

    override suspend fun addHomeworkTaskToDb(homeworkId: Int, taskId: Int?, isDone: Boolean, content: String): HomeworkTaskId {
        val id = taskId ?: (findLocalTaskId().toInt() - 1)
        homeworkDao.insertTask(DbHomeworkTask(
            id = id.toLong(),
            homeworkId = homeworkId.toLong(),
            content = content,
            isDone = isDone
        ))
        return id
    }

    override suspend fun uploadHomeworkTask(vppId: VppId, homeworkId: Int, content: String): Response<HomeworkModificationResult, Int?> {
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

    override suspend fun uploadTaskState(vppId: VppId, homeworkTaskId: Int, isDone: Boolean): Response<HomeworkModificationResult, Unit?> {
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

    override suspend fun setTaskStateToDb(homeworkTaskId: Int, isDone: Boolean) {
        val task = homeworkDao.getHomeworkTaskById(homeworkTaskId).first()
        homeworkDao.insertTask(task.copy(isDone = isDone))
    }

    override suspend fun uploadNewDocumentName(vppId: VppId, homeworkDocument: es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument, newName: String): Response<HomeworkModificationResult, Unit?> {
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

    override suspend fun renameDocumentInDb(homeworkDocument: es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument, newName: String) {
        homeworkDocumentDao.updateHomeworkDocumentFileName(homeworkDocument.documentId, newName)
    }

    override suspend fun deleteDocumentFromCloud(vppId: VppId, homeworkDocument: es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument): Response<HomeworkModificationResult, Unit?> {
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

    override suspend fun deleteDocumentFromDb(homeworkDocument: es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument) {
        homeworkDocumentDao.deleteHomeworkDocumentById(homeworkDocument.documentId)
    }

    override suspend fun editSharingStatusInDb(homework: CloudHomework, isPublic: Boolean) {
        homeworkDao.changePublic(homework.id, isPublic)
    }

    override suspend fun uploadNewSharingStatus(vppId: VppId, homework: CloudHomework, isPublic: Boolean): Response<HomeworkModificationResult, Unit?> {
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

    override suspend fun editHidingStatusInDb(homework: CloudHomework, hide: Boolean) {
        homeworkDao.changeHidden(homework.id, hide)
    }

    override suspend fun deleteHomeworkFromDb(homework: Homework) {
        homeworkDao.deleteHomework(homework.id)
    }

    override suspend fun deleteHomeworkFromCloud(vppId: VppId, homework: CloudHomework): Response<HomeworkModificationResult, Unit?> {
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

    override suspend fun getDocumentById(id: Int): es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument? {
        return homeworkDocumentDao.getHomeworkDocumentById(id)?.toModel(context)
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
    @SerializedName("public") val shareWithClass: Boolean,
    @SerializedName("tasks") val tasks: List<HomeRecordTask>,
    @SerializedName("documents") val documentIds: List<Int>
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

private data class HomeworkDocument(
    val content: ByteArray,
    val name: String,
    val extension: String,
    val id: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeworkDocument

        if (!content.contentEquals(other.content)) return false
        if (name != other.name) return false
        if (extension != other.extension) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + extension.hashCode()
        result = 31 * result + id
        return result
    }
}

private data class RenameDocumentRequest(
    @SerializedName("file_name") val name: String
)
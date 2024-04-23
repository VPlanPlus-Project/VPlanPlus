package es.jvbabi.vplanplus.feature.main_homework.shared.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.DbHomework
import es.jvbabi.vplanplus.data.model.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.data.source.database.dao.PreferredHomeworkNotificationTimeDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
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
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.NewTaskRecord
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.getRelativeStringResource
import es.jvbabi.vplanplus.util.sha256
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class HomeworkRepositoryImpl(
    private val homeworkDao: HomeworkDao,
    private val homeworkNotificationTimeDao: PreferredHomeworkNotificationTimeDao,
    private val classRepository: ClassRepository,
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
            .filter { it.type == ProfileType.STUDENT }
            .forEach { profile ->
                val `class` = classRepository.getClassById(profile.referenceId) ?: return@forEach
                val school = `class`.school
                val url: String

                if (profile.vppId != null) {
                    val token = vppIdRepository.getVppIdToken(profile.vppId) ?: return@forEach
                    vppIdNetworkRepository.authentication = BearerAuthentication(token)

                    url = "/api/$API_VERSION/user/me/homework"
                } else {
                    vppIdNetworkRepository.authentication = school.buildAuthentication()
                    url =
                        "/api/$API_VERSION/school/${school.schoolId}/class/${`class`.name}/homework"
                }

                val response = vppIdNetworkRepository.doRequest(url)

                if (response.response != HttpStatusCode.OK || response.data == null) return@forEach
                val data = Gson().fromJson(response.data, HomeworkResponse::class.java).homework

                homeworkDao
                    .getAll()
                    .first()
                    .filter { data.none { nd -> nd.id == it.homework.id } }
                    .filter { it.homework.id > 0 }
                    .filter { it.classes.schoolEntity.name == `class`.name }
                    .map { it.homework.id }
                    .forEach {
                        homeworkDao.deleteHomework(it)
                    }

                val existingHomework = getAll().first().filter { it.classes == `class` }
                val newHomework = data
                    .filter { profile.isDefaultLessonEnabled(it.vpId.toLong()) }
                    .filter { !existingHomework.any { eh -> eh.id == it.id } }
                    .filter { it.createdBy != profile.vppId?.id?.toLong() }

                val changedHomework = data
                    .filter { profile.isDefaultLessonEnabled(it.vpId.toLong()) }
                    .filter {
                        it.buildHash(`class`.name) != existingHomework.firstOrNull { eh -> eh.id == it.id && !eh.isHidden }
                            ?.buildHash()
                    }
                    .filter { it.createdBy != profile.vppId?.id?.toLong() }
                    .filter { newHomework.none { nh -> nh.id == it.id } }

                data.forEach forEachHomework@{ responseHomework ->
                    val isNewHomework = existingHomework.none { it.id == responseHomework.id }
                    val id = responseHomework.id
                    val createdBy = vppIdRepository.getVppId(responseHomework.createdBy, school, false)
                    val until = ZonedDateTimeConverter().timestampToZonedDateTime(responseHomework.until)

                    val ignoredTaskIds = mutableListOf<Long>()
                    val replacementTasks =
                        (homeworkDao
                            .getById(id.toInt()).first()
                            ?.tasks ?: emptyList())
                            .mapNotNull { dbTask ->
                                val new =
                                    responseHomework.tasks.firstOrNull { task -> task.id.toLong() == dbTask.id }
                                        ?: return@mapNotNull null
                                ignoredTaskIds.add(new.id.toLong())
                                NewTaskRecord(
                                    id = dbTask.id,
                                    content = new.content,
                                    done = new.done ?: dbTask.done,
                                )
                            }
                            .plus(
                                responseHomework.tasks
                                    .filter { !ignoredTaskIds.contains(it.id.toLong()) }
                                    .map { task ->
                                        val record = NewTaskRecord(
                                            id = task.id.toLong(),
                                            content = task.content,
                                            done = task.done ?: until.isBefore(ZonedDateTime.now()),
                                        )
                                        record
                                    }
                            )

                    val existingRecord = homeworkDao
                        .getById(responseHomework.id.toInt()).first()
                        ?.toModel()
                    homeworkDao.deleteTasksForHomework(responseHomework.id)

                    insertHomework(
                        id = id,
                        createdBy = createdBy,
                        shareWithClass = responseHomework.shareWithClass,
                        until = until,
                        `class` = `class`,
                        defaultLessonVpId = responseHomework.vpId.toLong(),
                        createdAt = ZonedDateTimeConverter().timestampToZonedDateTime(
                            responseHomework.createdAt
                        ),
                        allowCloudUpdate = false,
                        tasks = replacementTasks,
                        isHidden = (existingRecord?.isHidden ?: (isNewHomework && until.isBefore(ZonedDateTime.now())) && createdBy?.id != profile.vppId?.id),
                    )
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
                    val notificationRelevantChangedHomework = changedHomework.filter { ZonedDateTimeConverter().timestampToZonedDateTime(it.until).isAfter(ZonedDateTime.now()) }

                    if (notificationRelevantNewHomework.size == 1 && showNewNotification) {
                        val defaultLessons =
                            defaultLessonRepository.getDefaultLessonByClassId(`class`.classId)

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
                                vppIdRepository.getVppId(notificationRelevantNewHomework.first().createdBy, `class`.school, false)?.name
                                    ?: "Unknown",
                                defaultLessons.firstOrNull { it.vpId == notificationRelevantNewHomework.first().vpId.toLong() }?.subject
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
                    } else if (notificationRelevantChangedHomework.isNotEmpty()) {
                        notificationRepository.sendNotification(
                            channelId = CHANNEL_ID_HOMEWORK,
                            id = CHANNEL_DEFAULT_NOTIFICATION_ID_HOMEWORK,
                            title = stringRepository.getString(R.string.notification_homeworkChangedHomeworkTitle),
                            message = stringRepository.getPlural(
                                R.plurals.notification_homeworkChangedHomeworkContent,
                                notificationRelevantChangedHomework.size,
                                notificationRelevantChangedHomework.size
                            ),
                            icon = R.drawable.vpp,
                            priority = NotificationCompat.PRIORITY_LOW,
                            pendingIntent = pendingIntent,
                        )
                    }
                }
            }
        keyValueRepository.set(Keys.IS_HOMEWORK_UPDATE_RUNNING, "false")
        isUpdateRunning = false
    }

    override suspend fun getHomeworkByClassId(classId: UUID): Flow<List<Homework>> {
        return homeworkDao.getByClassId(classId).map {
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

    override suspend fun insertHomework(
        id: Long?,
        createdBy: VppId?,
        createdAt: ZonedDateTime,
        `class`: Classes,
        defaultLessonVpId: Long?,
        shareWithClass: Boolean,
        until: ZonedDateTime,
        tasks: List<NewTaskRecord>,
        allowCloudUpdate: Boolean,
        isHidden: Boolean
    ): HomeworkModificationResult {
        if (!allowCloudUpdate || createdBy == null) {
            val dbHomework = DbHomework(
                id = id ?: (findLocalId() - 1),
                classes = `class`.classId,
                createdAt = createdAt,
                until = until,
                defaultLessonVpId = defaultLessonVpId,
                createdBy = createdBy?.id,
                hidden = isHidden,
                isPublic = shareWithClass
            )
            homeworkDao.insert(dbHomework)
            tasks.forEach { newTask ->
                val dbHomeworkTask = DbHomeworkTask(
                    id = newTask.id ?: (findLocalTaskId() - 1),
                    homeworkId = dbHomework.id,
                    content = newTask.content,
                    done = newTask.done
                )
                homeworkDao.insertTask(dbHomeworkTask)
            }
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppIdToken =
            vppIdRepository.getVppIdToken(createdBy) ?: return HomeworkModificationResult.FAILED
        vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)
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

        val dbHomework = DbHomework(
            id = response.id.toLong(),
            classes = `class`.classId,
            createdAt = createdAt,
            until = until,
            defaultLessonVpId = defaultLessonVpId,
            createdBy = createdBy.id,
            isPublic = shareWithClass,
            hidden = isHidden
        )
        homeworkDao.insert(dbHomework)
        response.tasks.forEach {
            val dbHomeworkTask = DbHomeworkTask(
                id = it.id,
                homeworkId = dbHomework.id,
                content = it.content,
                done = false
            )
            homeworkDao.insertTask(dbHomeworkTask)
        }
        return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
    }

    override suspend fun deleteOrHideHomework(
        homework: Homework,
        onlyHide: Boolean
    ): HomeworkModificationResult {
        if (homework.id < 0) {
            homeworkDao.deleteHomework(homework.id)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

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
        return if (result.response == HttpStatusCode.OK) {
            homeworkDao.deleteHomework(homework.id)
            HomeworkModificationResult.SUCCESS_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
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
                done = false
            )
            homeworkDao.insertTask(dbHomeworkTask)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppId = vppIdRepository.getVppId(homework.createdBy!!.id.toLong(), homework.classes.school, false) ?: return HomeworkModificationResult.FAILED

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
            done = false
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
            .firstOrNull { it.isActive() && it.classes == homework.classes } // fixme: Use vpp.ID provided in parameter; this creates problems when multiple vpp.IDs are connected to the same class

        if (task.id < 0 || vppId == null) {
            val dbHomeworkTask =
                homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(done = done)
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
                homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(done = done)
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

    override suspend fun getHomeworkByTask(task: HomeworkTask): Homework {
        val homeworkId = homeworkDao.getHomeworkTaskById(task.id.toInt()).first().homeworkId
        return homeworkDao.getById(homeworkId.toInt()).first()!!.toModel()
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

    override suspend fun changeVisibility(homework: Homework): HomeworkModificationResult {
        homeworkDao.changeHidden(homework.id, !homework.isHidden)
        return HomeworkModificationResult.SUCCESS_OFFLINE
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

private data class HomeworkResponseRecord(
    @SerializedName("id") val id: Long,
    @SerializedName("vp_id") val vpId: Int,
    @SerializedName("due_to") val until: Long,
    @SerializedName("created_by") val createdBy: Long,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("public") val shareWithClass: Boolean,
    @SerializedName("tasks") val tasks: List<HomeRecordTask>
) {
    fun buildHash(className: String): String {
        return "$id$createdBy$createdAt$vpId$until$shareWithClass$className${tasks.joinToString { it.content }}".sha256()
            .lowercase()
    }
}

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
    @SerializedName("vp_id") val vpId: Long?,
    @SerializedName("public") val shareWithClass: Boolean,
    @SerializedName("due_to") val until: Long,
    @SerializedName("tasks") val tasks: List<String>
)

private data class AddHomeworkResponse(
    @SerializedName("id") val id: Int,
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
package es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PreferredHomeworkNotificationTime
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.util.UUID

interface HomeworkRepository {

    suspend fun getHomeworkByClassId(classId: UUID): Flow<List<Homework>>

    suspend fun getHomeworkById(homeworkId: Int): Flow<Homework?>

    suspend fun getAll(): Flow<List<Homework>>

    suspend fun insertHomework(
        id: Long? = null,
        profile: Profile,
        `class`: Classes,
        defaultLessonVpId: Long?,
        storeInCloud: Boolean,
        shareWithClass: Boolean,
        until: ZonedDateTime,
        tasks: List<NewTaskRecord>,
        isHidden: Boolean,
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        documentUris: List<Uri>
    ): HomeworkModificationResult

    suspend fun addNewTask(
        homework: Homework,
        content: String,
    ): HomeworkModificationResult

    suspend fun setTaskState(
        homework: Homework,
        task: HomeworkTask,
        done: Boolean
    ): HomeworkModificationResult

    suspend fun editTaskContent(
        task: HomeworkTask,
        newContent: String
    ): HomeworkModificationResult

    suspend fun removeOrHideHomework(
        homework: Homework,
        task: DeleteTask
    ): HomeworkModificationResult

    suspend fun deleteTask(
        task: HomeworkTask
    ): HomeworkModificationResult

    suspend fun findLocalId(): Long
    suspend fun findLocalTaskId(): Long
    suspend fun findLocalDocumentId(): Int

    suspend fun fetchHomework(sendNotification: Boolean)

    suspend fun getHomeworkByTask(task: HomeworkTask): Homework

    suspend fun changeShareStatus(homework: Homework): HomeworkModificationResult

    suspend fun updateDueDate(homework: Homework, newDate: ZonedDateTime): HomeworkModificationResult

    suspend fun clearCache()

    fun isUpdateRunning(): Boolean

    suspend fun setPreferredHomeworkNotificationTime(hour: Int, minute: Int, dayOfWeek: DayOfWeek)
    suspend fun removePreferredHomeworkNotificationTime(dayOfWeek: DayOfWeek)
    fun getPreferredHomeworkNotificationTimes(): Flow<List<PreferredHomeworkNotificationTime>>
}

enum class HomeworkModificationResult {
    FAILED,
    SUCCESS_OFFLINE,
    SUCCESS_ONLINE_AND_OFFLINE
}

data class NewTaskRecord(
    val content: String,
    val done: Boolean = false,
    val id: Long? = null
)

enum class DeleteTask {
    DELETE,
    HIDE,
    FORCE_DELETE_LOCALLY
}
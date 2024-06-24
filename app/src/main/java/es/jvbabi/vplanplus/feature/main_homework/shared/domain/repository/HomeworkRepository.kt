package es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PreferredHomeworkNotificationTime
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.util.UUID

interface HomeworkRepository {

    suspend fun getHomeworkByGroupId(groupId: Int): Flow<List<Homework>>

    suspend fun getHomeworkById(homeworkId: Int): Flow<Homework?>

    suspend fun getAll(): Flow<List<Homework>>

    suspend fun insertHomework(
        id: Long? = null,
        profile: ClassProfile,
        defaultLessonVpId: Int?,
        storeInCloud: Boolean,
        shareWithClass: Boolean,
        until: ZonedDateTime,
        tasks: List<NewTaskRecord>,
        isHidden: Boolean,
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        documentUris: List<Document>
    ): HomeworkModificationResult

    suspend fun addNewTask(
        profile: ClassProfile,
        homework: Homework,
        content: String,
    ): HomeworkModificationResult

    suspend fun setTaskState(
        profile: ClassProfile,
        homework: Homework,
        task: HomeworkTask,
        done: Boolean
    ): HomeworkModificationResult

    suspend fun editTaskContent(
        profile: ClassProfile,
        task: HomeworkTask,
        newContent: String
    ): HomeworkModificationResult

    suspend fun removeOrHideHomework(
        profile: ClassProfile,
        homework: Homework,
        task: DeleteTask
    ): HomeworkModificationResult

    suspend fun deleteTask(
        profile: ClassProfile,
        task: HomeworkTask
    ): HomeworkModificationResult

    suspend fun findLocalId(): Long
    suspend fun findLocalTaskId(): Long
    suspend fun findLocalDocumentId(): Int

    suspend fun fetchHomework(sendNotification: Boolean)

    suspend fun getHomeworkByTask(task: HomeworkTask): Homework

    suspend fun changeShareStatus(profile: ClassProfile, homework: Homework): HomeworkModificationResult

    suspend fun updateDueDate(profile: ClassProfile, homework: Homework, newDate: ZonedDateTime): HomeworkModificationResult

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


data class Document(
    val uri: Uri,
    val name: String = UUID.randomUUID().toString(),
    val extension: String
)
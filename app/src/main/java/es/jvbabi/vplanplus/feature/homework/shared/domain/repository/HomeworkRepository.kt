package es.jvbabi.vplanplus.feature.homework.shared.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import java.util.UUID

interface HomeworkRepository {

    suspend fun getHomeworkByClassId(classId: UUID): Flow<List<Homework>>

    suspend fun getHomeworkById(homeworkId: Int): Flow<Homework?>

    suspend fun getAll(): Flow<List<Homework>>

    suspend fun insertHomework(
        id: Long? = null,
        createdBy: VppId?,
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        `class`: Classes,
        defaultLessonVpId: Long,
        shareWithClass: Boolean,
        until: ZonedDateTime,
        tasks: List<NewTaskRecord>,
        allowCloudUpdate: Boolean,
        isHidden: Boolean
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

    suspend fun deleteOrHideHomework(
        homework: Homework,
        onlyHide: Boolean = false
    ): HomeworkModificationResult

    suspend fun deleteTask(
        task: HomeworkTask
    ): HomeworkModificationResult

    suspend fun findLocalId(): Long
    suspend fun findLocalTaskId(): Long

    suspend fun fetchData()

    suspend fun getHomeworkByTask(task: HomeworkTask): Homework

    suspend fun changeShareStatus(homework: Homework): HomeworkModificationResult
    suspend fun changeVisibility(homework: Homework): HomeworkModificationResult

    suspend fun clearCache()

    fun isUpdateRunning(): Boolean
}

enum class HomeworkModificationResult {
    FAILED,
    SUCCESS_OFFLINE,
    SUCCESS_ONLINE_AND_OFFLINE
}

data class NewTaskRecord(
    val content: String,
    val done: Boolean = false,
    val id: Long? = null,
    val individualId: Long? = null
)
package es.jvbabi.vplanplus.feature.homework.shared.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface HomeworkRepository {

    suspend fun getHomeworkByClassId(classId: UUID): Flow<List<Homework>>

    suspend fun getHomeworkById(homeworkId: Int): Flow<Homework?>

    suspend fun getAll(): Flow<List<Homework>>

    suspend fun insertHomework(
        createdBy: VppId?,
        createdAt: LocalDateTime = LocalDateTime.now(),
        `class`: Classes,
        defaultLessonVpId: Long,
        shareWithClass: Boolean,
        until: LocalDate,
        tasks: List<String>,
        allowCloudUpdate: Boolean
    ): HomeworkModificationResult

    suspend fun addNewTask(
        homework: Homework,
        content: String,
    ): HomeworkModificationResult

    @Deprecated("Use other instead")
    suspend fun upsertTask(task: HomeworkTask)

    @Deprecated("Don't use this")
    suspend fun findLocalId(): Long
    @Deprecated("Don't use this")
    suspend fun findLocalTaskId(): Long

    @Deprecated("Doesn't work")
    suspend fun fetchData()
}

enum class HomeworkModificationResult {
    SUCCESS_ONLINE_AND_OFFLINE,
    SUCCESS_OFFLINE,
    FAILED
}
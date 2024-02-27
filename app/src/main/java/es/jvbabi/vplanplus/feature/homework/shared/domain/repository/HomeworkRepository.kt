package es.jvbabi.vplanplus.feature.homework.shared.domain.repository

import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface HomeworkRepository {

    suspend fun getHomeworkByClassId(classId: UUID): Flow<List<Homework>>

    suspend fun getHomeworkById(homeworkId: Int): Flow<Homework>?

    suspend fun getAll(): Flow<List<Homework>>

    suspend fun insertHomeworkLocally(homework: Homework)

    suspend fun updateTask(task: HomeworkTask)

    suspend fun findLocalId(): Long
    suspend fun findLocalTaskId(): Long

    suspend fun fetchData()
}
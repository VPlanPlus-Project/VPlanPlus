package es.jvbabi.vplanplus.feature.homework.shared.data.repository

import es.jvbabi.vplanplus.data.model.DbHomework
import es.jvbabi.vplanplus.data.model.DbHomeworkTask
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class HomeworkRepositoryImpl(
    private val homeworkDao: HomeworkDao
): HomeworkRepository {
    override suspend fun getHomeworkByClassId(classId: UUID): Flow<List<Homework>> {
        return homeworkDao.getByClassId(classId).map {
            it.map { homework -> homework.toModel() }
        }
    }

    override suspend fun getHomeworkById(homeworkId: Int): Flow<Homework> {
        return homeworkDao.getById(homeworkId).map { it.toModel() }
    }

    override suspend fun insertHomeworkLocally(homework: Homework) {
        val dbHomework = DbHomework(
            id = homework.id,
            createdBy = homework.createdBy?.id,
            createdAt = homework.createdAt,
            defaultLessonVpId = homework.defaultLesson.vpId,
            until = homework.until,
            classes = homework.classes.classId
        )
        homeworkDao.insert(dbHomework)
        homework.tasks.forEach { task ->
            homeworkDao.insertTask(
                DbHomeworkTask(
                    id = task.id,
                    content = task.content,
                    done = task.done,
                    homeworkId = homework.id
                )
            )
        }
    }

    override suspend fun updateTask(task: HomeworkTask) {
        val homework = homeworkDao.getHomeworkTaskById(task.id).first().copy(
            done = task.done,
            content = task.content
        )
        homeworkDao.insertTask(homework)
    }

    override suspend fun findLocalId(): Long {
        val homework = homeworkDao.getAll().first().minByOrNull { it.homework.id }
        return (homework?.homework?.id?.toLong() ?: 0) - 1
    }

    override suspend fun findLocalTaskId(): Long {
        val task = homeworkDao.getAll().first().flatMap { it.tasks }.minByOrNull { it.id }
        return (task?.id?.toLong() ?: 0) - 1
    }
}
package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.source.database.dao.DefaultLessonDao
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import java.util.UUID

class DefaultLessonRepositoryImpl(
    private val defaultLessonDao: DefaultLessonDao
): DefaultLessonRepository {
    override suspend fun insert(defaultLesson: DbDefaultLesson): UUID {
        defaultLessonDao.insert(defaultLesson)
        return defaultLesson.id
    }

    override suspend fun getDefaultLessonByGroupId(groupId: Int): List<DefaultLesson> {
        return defaultLessonDao.getDefaultLessonByGroupId(groupId).map { dl -> dl.toModel() }
    }

    override suspend fun getDefaultLessonsBySchool(school: School): List<DefaultLesson> {
        return defaultLessonDao.getDefaultLessonsBySchool(school.id).map { dl -> dl.toModel() }
    }

    override suspend fun updateTeacherId(groupId: Int, vpId: Int, teacherId: UUID) {
        defaultLessonDao.updateTeacherId(groupId, vpId, teacherId)
    }

    override suspend fun deleteDefaultLesson(id: UUID) {
        defaultLessonDao.deleteById(id)
    }

    override suspend fun insertDefaultLesson(groupId: Int, vpId: Int, subject: String, teacherId: UUID) {
        defaultLessonDao.insert(
            DbDefaultLesson(
                id = UUID.randomUUID(),
                classId = groupId,
                vpId = vpId,
                subject = subject,
                teacherId = teacherId
            )
        )
    }
}
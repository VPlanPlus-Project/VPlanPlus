package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.source.database.dao.DefaultLessonDao
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import java.util.UUID

class DefaultLessonRepositoryImpl(
    private val defaultLessonDao: DefaultLessonDao
): DefaultLessonRepository {
    override suspend fun insert(defaultLesson: DbDefaultLesson): UUID {
        defaultLessonDao.insert(defaultLesson)
        return defaultLesson.defaultLessonId
    }

    override suspend fun getDefaultLessonByVpId(vpId: Long): DefaultLesson? {
        return defaultLessonDao.getDefaultLessonByVpId(vpId)?.toModel()
    }

    override suspend fun getDefaultLessonByClassId(classId: Long): List<DefaultLesson> {
        return defaultLessonDao.getDefaultLessonByClassId(classId).map { dl -> dl.toModel() }
    }
}
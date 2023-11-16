package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.DefaultLessonDao
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository

class DefaultLessonRepositoryImpl(
    private val defaultLessonDao: DefaultLessonDao
): DefaultLessonRepository {
    override suspend fun insert(vpId: Long, subject: String, teacherId: Long?, classId: Long): Long {
        return defaultLessonDao.insert(vpId, subject, teacherId, classId)
    }

    override suspend fun getDefaultLessonByVpId(vpId: Long): DefaultLesson? {
        return defaultLessonDao.getDefaultLessonByVpId(vpId)
    }
}
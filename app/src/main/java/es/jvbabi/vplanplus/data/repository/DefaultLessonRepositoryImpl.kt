package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.DefaultLessonDao
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository

class DefaultLessonRepositoryImpl(
    private val defaultLessonDao: DefaultLessonDao
): DefaultLessonRepository {
    override suspend fun createDefaultLesson(df: DefaultLesson) {
        defaultLessonDao.insertDefaultLesson(df)
    }
}
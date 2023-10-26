package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.DefaultLesson

interface DefaultLessonRepository {
    suspend fun createDefaultLesson(df: DefaultLesson)
    suspend fun updateDefaultLesson(df: DefaultLesson)
}
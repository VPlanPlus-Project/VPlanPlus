package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import java.util.UUID

interface DefaultLessonRepository {
    suspend fun insert(defaultLesson: DefaultLesson): UUID
    suspend fun getDefaultLessonByVpId(vpId: Long): DefaultLesson?
}
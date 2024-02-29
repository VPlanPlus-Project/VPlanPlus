package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import java.util.UUID

interface DefaultLessonRepository {
    suspend fun insert(defaultLesson: DbDefaultLesson): UUID

    @Deprecated("Insecure")
    suspend fun getDefaultLessonByVpId(vpId: Long): DefaultLesson?
    suspend fun getDefaultLessonByClassId(classId: UUID): List<DefaultLesson>
    suspend fun updateTeacherId(classId: UUID, vpId: Long, teacherId: UUID)
    suspend fun deleteDefaultLesson(id: UUID)
}
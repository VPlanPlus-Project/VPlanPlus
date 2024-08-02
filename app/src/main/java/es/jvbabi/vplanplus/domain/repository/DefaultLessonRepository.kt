package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.School
import java.util.UUID

interface DefaultLessonRepository {
    suspend fun insert(defaultLesson: DbDefaultLesson): UUID

    suspend fun getDefaultLessonByGroupId(groupId: Int): List<DefaultLesson>
    suspend fun getDefaultLessonsBySchool(school: School): List<DefaultLesson>
    suspend fun updateTeacherId(groupId: Int, vpId: Int, teacherId: UUID)
    suspend fun deleteDefaultLesson(id: UUID)

    suspend fun insertDefaultLesson(groupId: Int, vpId: Int, subject: String, teacherId: UUID, courseGroup: String?)
}
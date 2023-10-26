package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Teacher

interface TeacherRepository {
    suspend fun createTeacher(schoolId: String, acronym: String)
    suspend fun getTeachersBySchoolId(schoolId: String): List<Teacher>
    suspend fun find(schoolId: String, acronym: String): Teacher?
}
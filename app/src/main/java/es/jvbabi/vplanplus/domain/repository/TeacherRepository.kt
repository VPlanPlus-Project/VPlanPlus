package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher

interface TeacherRepository {
    suspend fun createTeacher(schoolId: Long, acronym: String)
    suspend fun getTeachersBySchoolId(schoolId: Long): List<Teacher>
    suspend fun find(school: School, acronym: String, createIfNotExists: Boolean = false): Teacher?
    suspend fun getTeacherById(id: Long): Teacher?
    suspend fun deleteTeachersBySchoolId(schoolId: Long)
    suspend fun insertTeachersByAcronym(schoolId: Long, teachers: List<String>)
}
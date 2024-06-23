package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import java.util.UUID

interface TeacherRepository {
    suspend fun createTeacher(schoolId: Int, acronym: String)
    suspend fun getTeachersBySchoolId(schoolId: Int): List<Teacher>
    suspend fun find(school: School, acronym: String, createIfNotExists: Boolean = false): Teacher?
    suspend fun getTeacherById(id: UUID): Teacher?
    suspend fun deleteTeachersBySchoolId(schoolId: Int)
    suspend fun insertTeachersByAcronym(schoolId: Int, teachers: List<String>)

    suspend fun getAll(): List<Teacher>
}
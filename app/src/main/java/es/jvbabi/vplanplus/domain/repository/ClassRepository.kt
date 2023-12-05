package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.School
import java.util.UUID

interface ClassRepository {

    suspend fun createClass(schoolId: Long, className: String)
    suspend fun getClassBySchoolIdAndClassName(schoolId: Long, className: String, createIfNotExists: Boolean = false): Classes?
    suspend fun getClassById(id: UUID): Classes
    suspend fun insertClasses(schoolId: Long, classes: List<String>)
    suspend fun deleteClassesBySchoolId(schoolId: Long)
    suspend fun getClassesBySchool(school: School): List<Classes>
}
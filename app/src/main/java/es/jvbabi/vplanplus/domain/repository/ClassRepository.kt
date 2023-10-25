package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes

interface ClassRepository {

    suspend fun createClass(schoolId: String, className: String)
    suspend fun getClassIdBySchoolIdAndClassName(schoolId: String, className: String): Int
    suspend fun getClassById(id: Int): Classes
}
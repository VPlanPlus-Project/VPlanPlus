package es.jvbabi.vplanplus.domain.repository

interface ClassRepository {

    suspend fun createClass(schoolId: String, className: String)
    suspend fun getClassIdBySchoolIdAndClassName(schoolId: String, className: String): Int
}
package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    fun getSchools(): Flow<List<School>>

    suspend fun checkSchoolId(schoolId: Long): SchoolIdCheckResult?
    suspend fun login(schoolId: Long, username: String, password: String): Response
    suspend fun createSchool(schoolId: Long, username: String, password: String, name: String)
    suspend fun updateSchoolName(schoolId: Long, name: String)
    suspend fun getSchoolNameOnline(schoolId: Long, username: String, password: String): String
    suspend fun getSchoolFromId(schoolId: Long): School
}
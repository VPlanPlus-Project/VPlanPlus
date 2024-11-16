package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.shared.data.SchoolInformation
import io.ktor.http.HttpStatusCode

interface SchoolRepository {
    suspend fun getSchools(): List<School>
    suspend fun checkSchoolId(schoolId: Int): SchoolIdCheckResult?
    suspend fun login(schoolId: Long, username: String, password: String): HttpStatusCode?

    suspend fun createSchool(
        schoolId: Int,
        sp24SchoolId: Int,
        username: String,
        password: String,
        name: String,
        daysPerWeek: Int,
        fullyCompatible: Boolean,
        schoolDownloadMode: SchoolDownloadMode
    )
    suspend fun updateSchoolName(schoolId: Int, name: String)
    suspend fun getSchoolNameOnline(schoolId: Int, username: String, password: String): String
    suspend fun getSchoolFromId(schoolId: Int): School?
    suspend fun getSchoolBySp24Id(sp24SchoolId: Int): School?
    suspend fun deleteSchool(schoolId: Int)
    suspend fun getSchoolByName(schoolName: String): School
    suspend fun updateCredentialsValid(school: School, credentialsValid: Boolean?)
    suspend fun updateCredentials(school: School, username: String, password: String)

    suspend fun getSchoolInfoBySp24DataOnline(sp24SchoolId: Int, username: String, password: String): SchoolInformation?
}

enum class SchoolIdCheckResult {
    VALID,
    NOT_FOUND
}
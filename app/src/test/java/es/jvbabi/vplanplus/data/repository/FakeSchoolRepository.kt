package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.SchoolSp24Access
import es.jvbabi.vplanplus.domain.repository.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.shared.data.SchoolInformation
import io.ktor.http.HttpStatusCode

private val existingSchoolIds = listOf(SchoolSp24Access(1, 10000000, "schueler", "pass"))

class FakeSchoolRepository(private val internetRepository: InternetRepository) : SchoolRepository {

    private val schools = mutableListOf<School>()

    override suspend fun getSchools(): List<School> {
        return schools
    }

    override suspend fun checkSchoolId(schoolId: Int): SchoolIdCheckResult? {
        val response = internetRepository.simulateNetworkCall(schoolId in existingSchoolIds.map { it.sp24SchoolId }) ?: return null
        return if (response) SchoolIdCheckResult.VALID
        else SchoolIdCheckResult.NOT_FOUND
    }

    override suspend fun login(
        schoolId: Long,
        username: String,
        password: String
    ): HttpStatusCode? {
        val result = run {
            val sp24School = existingSchoolIds.find { it.sp24SchoolId == schoolId.toInt() } ?: return@run HttpStatusCode.NotFound
            return@run if (sp24School.username == username && sp24School.password == password) HttpStatusCode.OK
            else HttpStatusCode.Unauthorized
        }
        return internetRepository.simulateNetworkCall(result)
    }

    override suspend fun createSchool(
        schoolId: Int,
        sp24SchoolId: Int,
        username: String,
        password: String,
        name: String,
        daysPerWeek: Int,
        fullyCompatible: Boolean
    ) {
        schools.add(School(schoolId, sp24SchoolId, username, password, name, daysPerWeek, fullyCompatible))
    }

    override suspend fun updateSchoolName(schoolId: Int, name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getSchoolNameOnline(
        schoolId: Int,
        username: String,
        password: String
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun getSchoolFromId(schoolId: Int): School? {
        return schools.find { it.id == schoolId }
    }

    override suspend fun getSchoolBySp24Id(sp24SchoolId: Int): School? {
        return schools.find { it.sp24SchoolId == sp24SchoolId }
    }

    override suspend fun deleteSchool(schoolId: Int) {
        schools.removeIf { it.id == schoolId }
    }

    override suspend fun getSchoolByName(schoolName: String): School {
        return schools.find { it.name == schoolName }!!
    }

    override suspend fun updateCredentialsValid(school: School, credentialsValid: Boolean?) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCredentials(school: School, username: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getSchoolInfoBySp24DataOnline(
        sp24SchoolId: Int,
        username: String,
        password: String
    ): SchoolInformation? {
        TODO("Not yet implemented")
    }
}
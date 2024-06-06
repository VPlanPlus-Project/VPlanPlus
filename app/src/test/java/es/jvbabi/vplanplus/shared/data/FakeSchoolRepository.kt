package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import io.ktor.http.HttpStatusCode

class FakeSchoolRepository : SchoolRepository {
    private val schools = mutableListOf<School>()

    override suspend fun getSchools(): List<School> {
        return schools
    }

    override suspend fun checkSchoolId(schoolId: Long): SchoolIdCheckResult {
        return if (schoolId in 10000000L..15000000) SchoolIdCheckResult.VALID else SchoolIdCheckResult.NOT_FOUND
    }

    override suspend fun login(schoolId: Long, username: String, password: String): HttpStatusCode {
        val school = schools.firstOrNull { it.schoolId == schoolId } ?: return HttpStatusCode.NotFound
        return if (school.username == username && school.password == password) HttpStatusCode.OK else HttpStatusCode.Forbidden
    }

    override suspend fun createSchool(
        schoolId: Long,
        username: String,
        password: String,
        name: String,
        daysPerWeek: Int,
        fullyCompatible: Boolean
    ) {
        schools.add(School(schoolId, name, username, password, daysPerWeek, fullyCompatible))
    }

    override suspend fun updateSchoolName(schoolId: Long, name: String) {}

    override suspend fun getSchoolNameOnline(
        schoolId: Long,
        username: String,
        password: String
    ): String {
        return schools.first { it.schoolId == schoolId }.name
    }

    override suspend fun getSchoolFromId(schoolId: Long): School? {
        return schools.firstOrNull { it.schoolId == schoolId }
    }

    override suspend fun deleteSchool(schoolId: Long) {
        schools.removeIf { it.schoolId == schoolId }
    }

    override suspend fun getSchoolByName(schoolName: String): School {
        return schools.first { it.name == schoolName }
    }

    override suspend fun updateCredentialsValid(school: School, credentialsValid: Boolean?) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCredentials(school: School, username: String, password: String) {
        TODO("Not yet implemented")
    }

    suspend fun createExampleData() {
        exampleSchools.forEach {
            createSchool(
                schoolId = it.schoolId,
                username = it.username,
                password = it.password,
                name = it.name,
                daysPerWeek = it.daysPerWeek,
                fullyCompatible = it.fullyCompatible
            )
        }
    }

    companion object {
        val exampleSchools = listOf(
            School(10000000, "Testschool", "example", "example", 5, true),
            School(10000001, "Albert-Einstein-Gymnasium", "schueler", "ein.stein", 5, true),
            School(10000002, "Gymnasium am Steinwald", "schueler", "steinwald", 5, false),
        )
    }
}
package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SchoolUseCases(
    private val schoolRepository: SchoolRepository
) {
    fun atLeastOneSchoolExists(): Flow<Boolean> {
        return schoolRepository.getSchools().map { schools -> schools.isNotEmpty() }
    }

    fun checkSchoolId(schoolId: String): SchoolIdCheckResult {
        return if (schoolId.length == 8 && schoolId.toIntOrNull() != null) SchoolIdCheckResult.SYNTACTICALLY_CORRECT else SchoolIdCheckResult.INVALID
    }

    suspend fun checkSchoolIdOnline(schoolId: String): Flow<SchoolIdCheckResult?> {
        return flowOf(schoolRepository.checkSchoolId(schoolId))
    }

    suspend fun login(schoolId: String, username: String, password: String): Response {
        return schoolRepository.login(schoolId, username, password)
    }

    suspend fun createSchool(schoolId: String, username: String, password: String) {
        schoolRepository.createSchool(schoolId, username, password)
    }
}

enum class SchoolIdCheckResult {
    INVALID,
    VALID,
    SYNTACTICALLY_CORRECT,
    NOT_FOUND
}

enum class Response {
    SUCCESS,
    WRONG_CREDENTIALS,
    NO_INTERNET,
    NONE,
    OTHER,
    NOT_FOUND
}
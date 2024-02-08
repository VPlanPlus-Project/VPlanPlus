package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.SchoolRepository

@Deprecated("This class is not used anymore")
class SchoolUseCases(
    private val schoolRepository: SchoolRepository
) {

    suspend fun deleteSchool(schoolId: Long) {
        schoolRepository.deleteSchool(schoolId)
    }

    suspend fun getSchoolByName(schoolName: String): School {
        return schoolRepository.getSchoolByName(schoolName)
    }

    suspend fun getSchools(): List<School> {
        return schoolRepository.getSchools()
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
    NOT_FOUND,
    NO_DATA_AVAILABLE
}
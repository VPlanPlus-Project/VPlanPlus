package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.SchoolIdCheckResult

class TestSchoolExistence(
    private val schoolRepository: SchoolRepository,
) {

    suspend operator fun invoke(schoolId: Long): SchoolIdCheckResult? {
        return schoolRepository.checkSchoolId(schoolId)
    }
}
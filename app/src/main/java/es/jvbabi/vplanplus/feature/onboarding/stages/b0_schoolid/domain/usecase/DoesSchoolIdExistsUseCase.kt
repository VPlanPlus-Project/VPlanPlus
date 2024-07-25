package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

import es.jvbabi.vplanplus.domain.repository.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.ui.SchoolIdError

class DoesSchoolIdExistsUseCase(
    private val schoolRepository: SchoolRepository
) {

    /**
     * Check if a stundenplan24.de school with the given ID exists.
     */
    suspend operator fun invoke(sp24SchoolId: Int): SchoolIdError? {
        return when (schoolRepository.checkSchoolId(sp24SchoolId)) {
            SchoolIdCheckResult.VALID -> null
            SchoolIdCheckResult.NOT_FOUND -> SchoolIdError.DOES_NOT_EXIST
            else -> SchoolIdError.NETWORK_ERROR
        }
    }
}
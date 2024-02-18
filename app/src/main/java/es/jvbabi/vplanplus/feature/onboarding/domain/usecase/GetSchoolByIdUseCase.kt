package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.SchoolRepository

class GetSchoolByIdUseCase(
    private val schoolRepository: SchoolRepository
) {
    suspend operator fun invoke(schoolId: Long): School? {
        return schoolRepository.getSchoolFromId(schoolId)
    }
}
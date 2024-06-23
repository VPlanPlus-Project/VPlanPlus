package es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class IsFirstProfileForSchoolUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(): Boolean {
        return keyValueRepository.get("onboarding.is_first_profile")?.toBoolean() ?: true
    }
}
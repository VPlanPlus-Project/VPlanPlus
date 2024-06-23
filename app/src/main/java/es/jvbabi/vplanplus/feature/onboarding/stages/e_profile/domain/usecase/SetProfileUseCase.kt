package es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class SetProfileUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(profile: String) {
        keyValueRepository.set("onboarding.profile", profile)
    }
}
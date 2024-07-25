package es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase

import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class SetProfileTypeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(profileType: ProfileType) {
        keyValueRepository.set("onboarding.profile_type", profileType.ordinal.toString())
    }
}
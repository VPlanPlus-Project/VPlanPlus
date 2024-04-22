package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class SetProfileVppIdUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile, vppId: VppId?) = profileRepository.setProfileVppId(profile, vppId)
}
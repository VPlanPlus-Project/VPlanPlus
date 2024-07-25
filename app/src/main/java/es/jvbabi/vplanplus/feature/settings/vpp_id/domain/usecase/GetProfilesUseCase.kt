package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.map

class GetProfilesUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke() = profileRepository.getProfiles().map {
        it.filter { profile -> profile is ClassProfile && profile.vppId != null }
    }
}
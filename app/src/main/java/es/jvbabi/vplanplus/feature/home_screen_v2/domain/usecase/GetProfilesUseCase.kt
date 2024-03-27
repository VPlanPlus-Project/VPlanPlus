package es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase

import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.map

class GetProfilesUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke() = profileRepository
        .getProfiles()
        .map { it.sortedBy { profile -> profile.displayName } }
}
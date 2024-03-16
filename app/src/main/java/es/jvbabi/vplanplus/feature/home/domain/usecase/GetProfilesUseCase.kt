package es.jvbabi.vplanplus.feature.home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.map

class GetProfilesUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke() = profileRepository
        .getProfiles()
        .map { it.sortedBy { profile -> profile.displayName } }
}
package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class UpdateProfileDisplayNameUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile, displayName: String) {
        profileRepository.updateProfile(
            (profileRepository.getDbProfileById(profileId = profile.id) ?: return)
                .copy(customName = displayName)
        )
    }
}
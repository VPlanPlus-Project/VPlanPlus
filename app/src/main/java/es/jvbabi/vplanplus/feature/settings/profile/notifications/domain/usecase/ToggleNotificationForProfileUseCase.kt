package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class ToggleNotificationForProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(profile: Profile, enabled: Boolean) {
        profileRepository.updateProfile(
            profile,
            isNotificationEnabled = enabled
        )
    }
}
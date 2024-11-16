package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class SetNotificationOnNewAssessmentUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(profile: ClassProfile, enabled: Boolean) {
        profileRepository.updateClassProfile(
            profile,
            notificationSettings = profile.notificationSettings.copy(
                onNewAssessmentEnabled = enabled
            )
        )
    }
}
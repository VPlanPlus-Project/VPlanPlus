package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.ClassProfileNotificationSetting
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first

class ToggleSendNotificationOnNewHomeworkUseCase(
    private val profileRepository: ProfileRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(enabled: Boolean) {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return
        profileRepository.updateClassProfile(
            profile,
            notificationSettings = ClassProfileNotificationSetting(
                previous = profile.notificationSettings,
                onNewHomeworkEnabled = enabled
            )
        )
    }
}
package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.ClassProfileNotificationSetting
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.daily.UpdateDailyNotificationAlarmsUseCase

class SetDailyReminderEnabledUseCase(
    private val profileRepository: ProfileRepository,
    private val updateDailyNotificationAlarmsUseCase: UpdateDailyNotificationAlarmsUseCase
) {
    suspend operator fun invoke(profile: ClassProfile, enabled: Boolean) {
        profileRepository.updateClassProfile(
            profile,
            notificationSettings = ClassProfileNotificationSetting(
                previous = profile.notificationSettings,
                isDailyNotificationEnabled = enabled
            )
        )
        updateDailyNotificationAlarmsUseCase()
    }
}
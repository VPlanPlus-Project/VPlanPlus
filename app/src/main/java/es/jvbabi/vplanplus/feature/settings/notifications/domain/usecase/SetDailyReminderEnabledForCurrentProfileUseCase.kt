package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.daily.UpdateDailyNotificationAlarmsUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first

class SetDailyReminderEnabledForCurrentProfileUseCase(
    private val profileRepository: ProfileRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val updateDailyNotificationAlarmsUseCase: UpdateDailyNotificationAlarmsUseCase
) {
    suspend operator fun invoke(enabled: Boolean) {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return
        profileRepository.setDailyNotificationEnabled(profile, enabled)
        updateDailyNotificationAlarmsUseCase()
    }
}
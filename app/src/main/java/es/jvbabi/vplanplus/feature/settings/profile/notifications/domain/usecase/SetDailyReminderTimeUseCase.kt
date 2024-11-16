package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.usecase.daily.UpdateDailyNotificationAlarmsUseCase
import java.time.DayOfWeek
import java.time.LocalTime

class SetDailyReminderTimeUseCase(
    private val dailyReminderRepository: DailyReminderRepository,
    private val updateDailyNotificationAlarmsUseCase: UpdateDailyNotificationAlarmsUseCase
) {
    suspend operator fun invoke(profile: Profile, dayOfWeek: DayOfWeek, time: LocalTime) {
        if (profile !is ClassProfile) return
        dailyReminderRepository.setDailyReminderTime(profile, dayOfWeek, time)
        updateDailyNotificationAlarmsUseCase()
    }
}
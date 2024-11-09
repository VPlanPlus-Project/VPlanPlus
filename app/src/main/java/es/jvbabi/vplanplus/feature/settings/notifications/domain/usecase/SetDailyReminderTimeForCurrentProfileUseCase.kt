package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalTime

class SetDailyReminderTimeForCurrentProfileUseCase(
    private val dailyReminderRepository: DailyReminderRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(dayOfWeek: Int, time: LocalTime) {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return
        dailyReminderRepository.setDailyReminderTime(profile, DayOfWeek.of(dayOfWeek), time)
    }
}
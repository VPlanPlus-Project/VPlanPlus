package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first

class SetDailyReminderEnabledForCurrentProfileUseCase(
    private val dailyReminderRepository: DailyReminderRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(enabled: Boolean) {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return
        dailyReminderRepository.setDailyReminderEnabled(profile, enabled)
    }

}
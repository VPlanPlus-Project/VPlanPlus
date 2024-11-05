package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class IsDailyReminderEnabledForCurrentProfileUseCase(
    private val dailyReminderRepository: DailyReminderRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Boolean> {
         return getCurrentProfileUseCase().flatMapLatest { profile ->
             if (profile !is ClassProfile) return@flatMapLatest flowOf(false)
             return@flatMapLatest dailyReminderRepository.isDailyReminderEnabled(profile)
         }
    }
}
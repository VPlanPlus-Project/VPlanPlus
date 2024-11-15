package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.time.DayOfWeek
import java.time.LocalTime

class GetDailyReminderTimeForCurrentProfileUseCase(
    private val dailyReminderRepository: DailyReminderRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Map<DayOfWeek, LocalTime>> {
        return getCurrentProfileUseCase().flatMapLatest { profile ->
            if (profile !is ClassProfile) return@flatMapLatest flowOf(emptyMap())
            flow {
                combine(
                    List(7) { dailyReminderRepository.getDailyReminderTime(profile, DayOfWeek.of(it+1)) },
                ) { times ->
                    times.mapIndexed { index, time -> DayOfWeek.of(index+1) to time }.toMap()
                }.collect { times ->
                    emit(times)
                }
            }
        }
    }
}
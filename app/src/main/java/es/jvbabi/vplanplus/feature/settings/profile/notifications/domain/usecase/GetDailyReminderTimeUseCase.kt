package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.time.DayOfWeek
import java.time.LocalTime

class GetDailyReminderTimeUseCase(
    private val dailyReminderRepository: DailyReminderRepository,
) {
    operator fun invoke(profile: Profile): Flow<Map<DayOfWeek, LocalTime>> {
        if (profile !is ClassProfile) return flowOf(emptyMap())
        return flow {
            combine(
                List(7) { dailyReminderRepository.getDailyReminderTime(profile, DayOfWeek.of(it + 1)) },
            ) { times ->
                times.mapIndexed { index, time -> DayOfWeek.of(index + 1) to time }.toMap()
            }.collect { times ->
                emit(times)
            }
        }
    }
}
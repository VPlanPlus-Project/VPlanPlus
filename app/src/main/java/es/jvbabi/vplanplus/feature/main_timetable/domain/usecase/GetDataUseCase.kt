package es.jvbabi.vplanplus.feature.main_timetable.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class GetDataUseCase(
    val planRepository: PlanRepository,
    val keyValueRepository: KeyValueRepository,
    val getActiveProfileUseCase: GetCurrentProfileUseCase
) {
    val data = TimetableData()

    operator fun invoke(dates: Set<LocalDate>) = flow {
        combine(
            getActiveProfileUseCase(),
            keyValueRepository.getFlow(Keys.LESSON_VERSION_NUMBER)
        ) { profile, version ->
            if (profile == null) return@combine data
            var days = data.days.keys.associateWith {
                planRepository.getDayForProfile(profile, it, version?.toLongOrNull() ?: 0).first()
            }
            dates.forEach { date ->
                if (!days.containsKey(date)) {
                    days = days.plus(date to planRepository.getDayForProfile(profile, date, version?.toLongOrNull() ?: 0).first())
                }
            }
            data.copy(
                profile = profile,
                version = version?.toLongOrNull() ?: 0,
                days = days
            )
        }.collect {
            emit(it)
        }
    }
}
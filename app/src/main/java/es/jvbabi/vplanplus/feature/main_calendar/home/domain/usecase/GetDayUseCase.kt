package es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetDayUseCase(
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(date: LocalDate, profile: Profile) = flow {
        keyValueRepository
            .getFlowOrDefault(Keys.LESSON_VERSION_NUMBER, "0")
            .map { it.toLong() }
            .collect { version ->
                val day = planRepository.getDayForProfile(profile, date, version).first()
                emit(SchoolDay(
                    date = date,
                    info = day.info,
                    lessons = day.lessons
                ))
            }
    }
}
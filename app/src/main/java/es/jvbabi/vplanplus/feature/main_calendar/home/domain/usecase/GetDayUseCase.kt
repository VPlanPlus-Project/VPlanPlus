package es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetDayUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class GetDayUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val getDayUseCase: GetDayUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(date: LocalDate): Flow<SchoolDay> {
        return getCurrentProfileUseCase().flatMapLatest { profile ->
            if (profile == null) return@flatMapLatest flowOf(SchoolDay(date))
            return@flatMapLatest getDayUseCase(date, profile)
        }
    }
}
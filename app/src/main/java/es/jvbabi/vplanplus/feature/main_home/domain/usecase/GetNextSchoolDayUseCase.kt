package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetNextDayUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GetNextSchoolDayUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val getNextDayUseCase: GetNextDayUseCase,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(fast: Boolean = true): Flow<SchoolDay?> {
        return getCurrentProfileUseCase()
            .flatMapLatest { profile ->
                if (profile == null) return@flatMapLatest flowOf(null)
                return@flatMapLatest getNextDayUseCase(profile, fast = fast)
            }
    }
}
package es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetLastSyncUseCase

data class CalendarViewUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getDayUseCase: GetDayUseCase,
    val getLastSyncUseCase: GetLastSyncUseCase
)

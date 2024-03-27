package es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase

data class HomeUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getCurrentTimeUseCase: GetCurrentTimeUseCase,
    val getDayUseCase: GetDayUseCase,
    val getHomeworkUseCase: GetHomeworkUseCase,
    val getRoomBookingsForTodayUseCase: GetRoomBookingsForTodayUseCase,

    val setInfoExpandedUseCase: SetInfoExpandedUseCase,
    val isInfoExpandedUseCase: IsInfoExpandedUseCase
)

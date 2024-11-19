package es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsBalloonUseCase
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetCurrentDataVersionUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetLastSyncUseCase

data class CalendarViewUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getDayUseCase: GetDayUseCase,
    val getLastSyncUseCase: GetLastSyncUseCase,

    val canShowTimetableInfoBannerUseCase: CanShowTimetableInfoBannerUseCase,
    val dismissTimetableInfoBannerUseCase: DismissTimetableInfoBannerUseCase,

    val getCurrentDataVersionUseCase: GetCurrentDataVersionUseCase,
    val isBalloonUseCase: IsBalloonUseCase,
    val setBalloonUseCase: SetBalloonUseCase
)

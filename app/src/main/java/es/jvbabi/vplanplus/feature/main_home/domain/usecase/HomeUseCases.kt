package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase

data class HomeUseCases(
    val getProfilesUseCase: GetProfilesUseCase,
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getDayForCurrentProfileUseCase: GetDayForCurrentProfileUseCase,
    val getLastSyncUseCase: GetLastSyncUseCase,
    val getCurrentTimeUseCase: GetCurrentTimeUseCase,
    val getHomeworkUseCase: GetHomeworkUseCase,
    val isInfoExpandedUseCase: IsInfoExpandedUseCase,
    val setInfoExpandedUseCase: SetInfoExpandedUseCase,
    val changeProfileUseCase: ChangeProfileUseCase,
    val isSyncRunningUseCase: IsSyncRunningUseCase,
    val getVersionHintsUseCase: GetVersionHintsUseCase,
    val updateLastVersionHintsVersionUseCase: UpdateLastVersionHintsVersionUseCase,
    val getRoomBookingsForTodayUseCase: GetRoomBookingsForTodayUseCase,
)

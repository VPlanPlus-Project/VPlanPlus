package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase

data class HomeUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getCurrentTimeUseCase: GetCurrentTimeUseCase,
    val getProfilesUseCase: GetProfilesUseCase,
    val changeProfileUseCase: ChangeProfileUseCase,
    val getDayUseCase: GetDayUseCase,
    val getHomeworkUseCase: GetHomeworkUseCase,
    val getRoomBookingsForTodayUseCase: GetRoomBookingsForTodayUseCase,
    val isSyncRunningUseCase: IsSyncRunningUseCase,
    val getLastSyncUseCase: GetLastSyncUseCase,
    val getHideFinishedLessonsUseCase: GetHideFinishedLessonsUseCase,

    val setInfoExpandedUseCase: SetInfoExpandedUseCase,
    val isInfoExpandedUseCase: IsInfoExpandedUseCase,

    val hasUnreadNewsUseCase: HasUnreadNewsUseCase,

    val getVersionHintsUseCase: GetVersionHintsUseCase,
    val updateLastVersionHintsVersionUseCase: UpdateLastVersionHintsVersionUseCase,

    val hasInvalidVppIdSessionUseCase: HasInvalidVppIdSessionUseCase,
    val ignoreInvalidVppIdSessionsUseCase: IgnoreInvalidVppIdSessionsUseCase,

    val getVppIdServerUseCase: GetVppIdServerUseCase
)

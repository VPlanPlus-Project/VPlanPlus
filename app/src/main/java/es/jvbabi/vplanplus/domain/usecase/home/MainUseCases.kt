package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase

data class MainUseCases(
    val getColorSchemeUseCase: GetColorSchemeUseCase,
    val getProfilesUseCase: GetProfilesUseCase,
    val getCurrentIdentity: GetCurrentProfileUseCase,
    val setCurrentProfileUseCase: SetCurrentProfileUseCase,
    val setUpUseCase: SetUpUseCase,
    val getHomeworkUseCase: GetHomeworkUseCase,
    val getAppThemeUseCase: GetAppThemeUseCase,
    val getSyncIntervalMinutesUseCase: GetSyncIntervalMinutesUseCase,
)

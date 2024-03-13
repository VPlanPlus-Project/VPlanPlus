package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
import es.jvbabi.vplanplus.feature.home.domain.usecase.UpdateLastVersionHintsVersionUseCase

data class MainUseCases(
    val getColorSchemeUseCase: GetColorSchemeUseCase,
    val getProfilesUseCase: GetProfilesUseCase,
    val getCurrentIdentity: GetCurrentIdentityUseCase,
    val setUpUseCase: SetUpUseCase,
    val getHomeworkUseCase: GetHomeworkUseCase,
    val getVersionHintsUseCase: GetVersionHintsUseCase,
    val updateLastVersionHintsVersionUseCase: UpdateLastVersionHintsVersionUseCase,
)

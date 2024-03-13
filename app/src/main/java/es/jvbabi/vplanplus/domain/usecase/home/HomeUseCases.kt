package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
import es.jvbabi.vplanplus.feature.home.domain.usecase.IsInfoExpandedUseCase
import es.jvbabi.vplanplus.feature.home.domain.usecase.SetInfoExpandedUseCase
import es.jvbabi.vplanplus.feature.home.domain.usecase.UpdateLastVersionHintsVersionUseCase

data class HomeUseCases(
    val getColorSchemeUseCase: GetColorSchemeUseCase,
    val getProfilesUseCase: GetProfilesUseCase,
    val getCurrentIdentity: GetCurrentIdentityUseCase,
    val setUpUseCase: SetUpUseCase,
    val isInfoExpandedUseCase: IsInfoExpandedUseCase,
    val setInfoExpandedUseCase: SetInfoExpandedUseCase,
    val getHomeworkUseCase: GetHomeworkUseCase,
    val getVersionHintsUseCase: GetVersionHintsUseCase,
    val updateLastVersionHintsVersionUseCase: UpdateLastVersionHintsVersionUseCase,
)

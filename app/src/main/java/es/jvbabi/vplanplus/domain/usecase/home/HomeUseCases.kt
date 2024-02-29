package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase

data class HomeUseCases(
    val getColorSchemeUseCase: GetColorSchemeUseCase,
    val getProfilesUseCase: GetProfilesUseCase,
    val getCurrentIdentity: GetCurrentIdentityUseCase,
    val setUpUseCase: SetUpUseCase,
)

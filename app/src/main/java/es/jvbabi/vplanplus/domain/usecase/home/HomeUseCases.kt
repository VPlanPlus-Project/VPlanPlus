package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase

data class HomeUseCases(
    val getColorSchemeUseCase: GetColorSchemeUseCase,
    val getCurrentIdentity: GetCurrentIdentityUseCase
)

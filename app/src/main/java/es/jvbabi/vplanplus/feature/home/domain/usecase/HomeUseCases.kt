package es.jvbabi.vplanplus.feature.home.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase

data class HomeUseCases(
    val getProfilesUseCase: GetProfilesUseCase,
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
)

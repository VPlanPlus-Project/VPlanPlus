package es.jvbabi.vplanplus.feature.onboarding.stages.a_welcome.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase

data class OnboardingWelcomeUseCases(
    val getVppIdServerUseCase: GetVppIdServerUseCase
)

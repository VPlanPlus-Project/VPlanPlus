package es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.domain.usecase

import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.IsFirstProfileForSchoolUseCase

data class OnboardingSetupUseCases(
    val setupUseCase: SetupUseCase,
    val isFirstProfileForSchoolUseCase: IsFirstProfileForSchoolUseCase
)

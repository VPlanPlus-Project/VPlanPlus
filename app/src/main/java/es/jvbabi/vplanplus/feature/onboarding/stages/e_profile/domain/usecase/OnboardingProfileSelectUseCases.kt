package es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.domain.usecase

import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.IsFirstProfileForSchoolUseCase

data class OnboardingProfileSelectUseCases(
    val getProfileOptionsUseCase: GetProfileOptionsUseCase,
    val isFirstProfileForSchoolUseCase: IsFirstProfileForSchoolUseCase,
    val setProfileUseCase: SetProfileUseCase
)
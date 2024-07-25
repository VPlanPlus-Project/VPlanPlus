package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

data class OnboardingSchoolIdUseCases(
    val isSchoolIdValidUseCase: IsSchoolIdValidUseCase,
    val doesSchoolIdExistsUseCase: DoesSchoolIdExistsUseCase,
    val setSchoolIdUseCase: SetSchoolIdUseCase
)

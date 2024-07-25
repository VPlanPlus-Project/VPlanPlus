package es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase

data class OnboardingCredentialsUseCases(
    val checkCredentialsAndInitOnboardingForSchoolUseCase: CheckCredentialsAndInitOnboardingForSchoolUseCase,
    val getSp24SchoolIdUseCase: GetSp24SchoolIdUseCase
)
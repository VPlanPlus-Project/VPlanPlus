package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

data class OnboardingUseCases(
    val checkSchoolIdSyntax: CheckSchoolIdSyntax,
    val testSchoolExistence: TestSchoolExistence,
    val loginUseCase: LoginUseCase,
    val profileOptionsUseCase: ProfileOptionsUseCase,
    val defaultLessonUseCase: DefaultLessonUseCase,
    val saveProfileUseCase: SaveProfileUseCase,
    val getSchoolByIdUseCase: GetSchoolByIdUseCase,
)

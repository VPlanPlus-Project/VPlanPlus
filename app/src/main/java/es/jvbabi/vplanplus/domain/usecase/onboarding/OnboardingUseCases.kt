package es.jvbabi.vplanplus.domain.usecase.onboarding

data class OnboardingUseCases(
    val checkSchoolIdSyntax: CheckSchoolIdSyntax,
    val testSchoolExistence: TestSchoolExistence,
    val loginUseCase: LoginUseCase,
    val profileOptionsUseCase: ProfileOptionsUseCase,
    val defaultLessonUseCase: DefaultLessonUseCase,
    val saveProfileUseCase: SaveProfileUseCase,
    val getSchoolByIdUseCase: GetSchoolByIdUseCase,
)

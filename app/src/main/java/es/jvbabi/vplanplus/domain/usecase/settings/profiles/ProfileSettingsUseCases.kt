package es.jvbabi.vplanplus.domain.usecase.settings.profiles

data class ProfileSettingsUseCases(
    val getProfilesUseCase: GetProfilesUseCase,
    val deleteSchoolUseCase: DeleteSchoolUseCase,
)
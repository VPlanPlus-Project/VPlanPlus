package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.usecase.settings.profiles.shared.GetProfileByIdUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.CheckCredentialsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.UpdateCredentialsUseCase

data class ProfileSettingsUseCases(
    val getProfilesUseCase: GetProfilesUseCase,
    val deleteSchoolUseCase: DeleteSchoolUseCase,
    val getProfileByIdUseCase: GetProfileByIdUseCase,
    val getCalendarsUseCase: GetCalendarsUseCase,
    val updateCalendarTypeUseCase: UpdateCalendarTypeUseCase,
    val updateCalendarIdUseCase: UpdateCalendarIdUseCase,
    val updateProfileDisplayNameUseCase: UpdateProfileDisplayNameUseCase,
    val deleteProfileUseCase: DeleteProfileUseCase,
    val checkCredentialsUseCase: CheckCredentialsUseCase,
    val updateCredentialsUseCase: UpdateCredentialsUseCase,
)
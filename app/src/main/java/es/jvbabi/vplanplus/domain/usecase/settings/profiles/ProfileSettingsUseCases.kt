package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.usecase.settings.profiles.shared.GetProfileByIdUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.CheckCredentialsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.UpdateCredentialsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.HasProfileLocalAssessmentsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.HasProfileLocalHomeworkUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateAssessmentsEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateHomeworkEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.ToggleNotificationForProfileUseCase

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
    val hasProfileLocalHomeworkUseCase: HasProfileLocalHomeworkUseCase,
    val updateHomeworkEnabledUseCase: UpdateHomeworkEnabledUseCase,

    val hasProfileLocalAssessmentsUseCase: HasProfileLocalAssessmentsUseCase,
    val updateAssessmentsEnabledUseCase: UpdateAssessmentsEnabledUseCase,

    val toggleNotificationForProfileUseCase: ToggleNotificationForProfileUseCase,
)
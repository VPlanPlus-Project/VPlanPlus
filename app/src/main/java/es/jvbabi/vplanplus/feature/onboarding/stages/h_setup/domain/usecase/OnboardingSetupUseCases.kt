package es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.settings.profiles.shared.GetProfileByIdUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.IsFirstProfileForSchoolUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateAssessmentsEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateHomeworkEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase.ToggleNotificationForProfileUseCase

data class OnboardingSetupUseCases(
    val setupUseCase: SetupUseCase,
    val isFirstProfileForSchoolUseCase: IsFirstProfileForSchoolUseCase,
    val getProfileByIdUseCase: GetProfileByIdUseCase,
    val toggleNotificationForProfileUseCase: ToggleNotificationForProfileUseCase,
    val updateHomeworkEnabledUseCase: UpdateHomeworkEnabledUseCase,
    val updateAssessmentsEnabledUseCase: UpdateAssessmentsEnabledUseCase
)

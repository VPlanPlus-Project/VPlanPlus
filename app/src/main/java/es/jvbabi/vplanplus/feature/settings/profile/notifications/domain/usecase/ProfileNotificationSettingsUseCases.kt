package es.jvbabi.vplanplus.feature.settings.profile.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.daily.SendNotificationUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsDeveloperModeEnabledUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.shared.GetProfileByIdUseCase

data class ProfileNotificationSettingsUseCases(
    val getProfileByIdUseCase: GetProfileByIdUseCase,
    val isDeveloperModeEnabledUseCase: IsDeveloperModeEnabledUseCase,

    val canSendProfileNotificationsUseCase: CanSendProfileNotificationsUseCase,

    val toggleNotificationForProfileUseCase: ToggleNotificationForProfileUseCase,

    val toggleSendNotificationOnNewHomeworkUseCase: ToggleSendNotificationOnNewHomeworkUseCase,

    val setNotificationOnNewAssessmentUseCase: SetNotificationOnNewAssessmentUseCase,

    val setNotificationOnNewPlanUseCase: SetNotificationOnNewPlanUseCase,

    val setDailyReminderEnabledUseCase: SetDailyReminderEnabledUseCase,
    val getDailyReminderTimeUseCase: GetDailyReminderTimeUseCase,
    val setDailyReminderTimeUseCase: SetDailyReminderTimeUseCase,
    val sendNotificationUseCase: SendNotificationUseCase
)

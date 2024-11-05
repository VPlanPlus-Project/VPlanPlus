package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.daily.SendNotificationUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsNotificationsEnabledUseCase

data class NotificationSettingsUseCases(
    val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase,

    val isDailyReminderEnabledUseCase: IsDailyReminderEnabledForCurrentProfileUseCase,
    val setDailyReminderEnabledUseCase: SetDailyReminderEnabledForCurrentProfileUseCase,
    val getDailyReminderTimeUseCase: GetDailyReminderTimeForCurrentProfileUseCase,
    val setDailyReminderTimeUseCase: SetDailyReminderTimeForCurrentProfileUseCase,
    val sendNotificationUseCase: SendNotificationUseCase
)
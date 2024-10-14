package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.IsNotificationsEnabledUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.TriggerNdpReminderNotificationUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.UpdateDynamicTimesUseCase

data class NotificationSettingsUseCases(
    val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase,

    val isAutomaticReminderTimeEnabledUseCase: IsAutomaticReminderTimeEnabledUseCase,
    val setAutomaticReminderTimeEnabledUseCase: SetAutomaticReminderTimeEnabledUseCase,

    val developerUpdateDynamicTimesUseCase: UpdateDynamicTimesUseCase,
    val developerTriggerNdpReminderNotificationUseCase: TriggerNdpReminderNotificationUseCase
)
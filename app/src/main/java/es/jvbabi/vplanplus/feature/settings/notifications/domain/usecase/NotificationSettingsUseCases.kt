package es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.IsNotificationsEnabledUseCase

data class NotificationSettingsUseCases(
    val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase,

    val isNotificationOnNewHomeworkEnabledUseCase: IsNotificationOnNewHomeworkEnabledUseCase,
    val setShowNotificationOnNewHomeworkUseCase: SetShowNotificationOnNewHomeworkUseCase
)
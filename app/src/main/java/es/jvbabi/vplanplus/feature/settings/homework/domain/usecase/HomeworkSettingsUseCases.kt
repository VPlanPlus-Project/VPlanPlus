package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.IsNotificationOnNewHomeworkEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.notifications.domain.usecase.SetShowNotificationOnNewHomeworkUseCase

data class HomeworkSettingsUseCases(
    var isNotificationOnNewHomeworkEnabledUseCase: IsNotificationOnNewHomeworkEnabledUseCase,
    var setShowNotificationOnNewHomeworkUseCase: SetShowNotificationOnNewHomeworkUseCase,
    val isRemindOnUnfinishedHomeworkUseCase: IsRemindOnUnfinishedHomeworkUseCase,
    val setRemindOnUnfinishedHomeworkUseCase: SetRemindOnUnfinishedHomeworkUseCase,
    val getDefaultNotificationTimeUseCase: GetDefaultNotificationTimeUseCase,
    val setDefaultNotificationTimeUseCase: SetDefaultNotificationTimeUseCase,
    val getPreferredHomeworkNotificationTimeUseCase: GetPreferredHomeworkNotificationTimeUseCase,
    val setPreferredHomeworkNotificationTimeUseCase: SetPreferredHomeworkNotificationTimeUseCase,
    val removePreferredHomeworkNotificationTimeUseCase: RemovePreferredHomeworkNotificationTimeUseCase,
    val canSendNotificationUseCase: CanSendNotificationUseCase
)

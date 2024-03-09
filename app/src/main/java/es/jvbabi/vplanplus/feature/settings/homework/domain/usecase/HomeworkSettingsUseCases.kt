package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

data class HomeworkSettingsUseCases(
    var isShowNotificationOnNewHomeworkUseCase: IsShowNotificationOnNewHomeworkUseCase,
    var setShowNotificationOnNewHomeworkUseCase: SetShowNotificationOnNewHomeworkUseCase,
    val isRemindOnUnfinishedHomeworkUseCase: IsRemindOnUnfinishedHomeworkUseCase,
    val setRemindOnUnfinishedHomeworkUseCase: SetRemindOnUnfinishedHomeworkUseCase,
    val getDefaultNotificationTimeUseCase: GetDefaultNotificationTimeUseCase,
    val setDefaultNotificationTimeUseCase: SetDefaultNotificationTimeUseCase,
    val getPreferredHomeworkNotificationTimeUseCase: GetPreferredHomeworkNotificationTimeUseCase,
    val setPreferredHomeworkNotificationTimeUseCase: SetPreferredHomeworkNotificationTimeUseCase,
    val removePreferredHomeworkNotificationTimeUseCase: RemovePreferredHomeworkNotificationTimeUseCase
)

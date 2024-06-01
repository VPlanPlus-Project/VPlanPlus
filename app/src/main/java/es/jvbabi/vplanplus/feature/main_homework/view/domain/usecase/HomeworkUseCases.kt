package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateHomeworkEnabledUseCase

data class HomeworkUseCases(
    val getHomeworkUseCase: GetHomeworkUseCase,
    val markAllDoneUseCase: MarkAllDoneUseCase,
    val markSingleDoneUseCase: MarkSingleDoneUseCase,
    val addTaskUseCase: AddTaskUseCase,
    val deleteHomeworkUseCase: DeleteHomeworkUseCase,
    val changeVisibilityUseCase: ChangeVisibilityUseCase,
    val deleteHomeworkTaskUseCase: DeleteHomeworkTaskUseCase,
    val editTaskUseCase: EditTaskUseCase,
    val isUpdateRunningUseCase: IsUpdateRunningUseCase,
    val updateUseCase: UpdateUseCase,
    val hideHomeworkUseCase: HideHomeworkUseCase,
    val showHomeworkNotificationBannerUseCase: ShowHomeworkNotificationBannerUseCase,
    val hideHomeworkNotificationBannerUseCase: HideHomeworkNotificationBannerUseCase,
    val updateDueDateUseCase: UpdateDueDateUseCase,
    val updateHomeworkEnabledUseCase: UpdateHomeworkEnabledUseCase
)

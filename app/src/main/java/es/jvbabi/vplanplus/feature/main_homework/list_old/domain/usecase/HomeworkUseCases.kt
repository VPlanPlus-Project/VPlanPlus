package es.jvbabi.vplanplus.feature.main_homework.list_old.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.DeleteHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ToggleHomeworkHiddenStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateHomeworkEnabledUseCase

data class HomeworkUseCases(
    val getHomeworkUseCase: GetHomeworkUseCase,
    val markAllDoneUseCase: MarkAllDoneUseCase,
    val markSingleDoneUseCase: ChangeTaskDoneStateUseCase,
    val addTaskUseCase: AddTaskUseCase,
    val deleteHomeworkUseCase: DeleteHomeworkUseCase,
    val changeVisibilityUseCase: ChangeVisibilityUseCase,
    val deleteHomeworkTaskUseCase: DeleteHomeworkTaskUseCase,
    val editTaskUseCase: EditTaskUseCase,
    val updateUseCase: UpdateUseCase,
    val toggleHomeworkHiddenStateUseCase: ToggleHomeworkHiddenStateUseCase,
    val showHomeworkNotificationBannerUseCase: ShowHomeworkNotificationBannerUseCase,
    val hideHomeworkNotificationBannerUseCase: HideHomeworkNotificationBannerUseCase,
    val updateDueDateUseCase: UpdateDueDateUseCase,
    val updateHomeworkEnabledUseCase: UpdateHomeworkEnabledUseCase
)

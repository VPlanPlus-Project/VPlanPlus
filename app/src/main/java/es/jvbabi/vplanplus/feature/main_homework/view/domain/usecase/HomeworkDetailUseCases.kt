package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase

data class HomeworkDetailUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getHomeworkByIdUseCase: GetHomeworkByIdUseCase,
    val taskDoneUseCase: ChangeTaskDoneStateUseCase,
    val updateDueDateUseCase: UpdateDueDateUseCase,
    val deleteHomeworkTaskUseCase: DeleteHomeworkTaskUseCase,
    val editTaskUseCase: EditTaskUseCase,
    val addTaskUseCase: AddTaskUseCase,
    val updateHomeworkVisibilityUseCase: UpdateHomeworkVisibilityUseCase,
    val updateDocumentsUseCase: UpdateDocumentsUseCase,
)

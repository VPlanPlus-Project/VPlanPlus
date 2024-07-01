package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.AddTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.DeleteHomeworkTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.EditTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.MarkSingleDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.UpdateDueDateUseCase

data class HomeworkDetailUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getHomeworkByIdUseCase: GetHomeworkByIdUseCase,
    val taskDoneUseCase: MarkSingleDoneUseCase,
    val updateDueDateUseCase: UpdateDueDateUseCase,
    val deleteHomeworkTaskUseCase: DeleteHomeworkTaskUseCase,
    val editTaskUseCase: EditTaskUseCase,
    val addTaskUseCase: AddTaskUseCase,

    val updateDocumentsUseCase: UpdateDocumentsUseCase,
)
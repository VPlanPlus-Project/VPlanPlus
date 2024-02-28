package es.jvbabi.vplanplus.feature.homework.view.domain.usecase

data class HomeworkUseCases(
    val getHomeworkUseCase: GetHomeworkUseCase,
    val markAllDoneUseCase: MarkAllDoneUseCase,
    val markSingleDoneUseCase: MarkSingleDoneUseCase,
    val addTaskUseCase: AddTaskUseCase,
    val deleteHomeworkUseCase: DeleteHomeworkUseCase,
    val changeVisibilityUseCase: ChangeVisibilityUseCase
)

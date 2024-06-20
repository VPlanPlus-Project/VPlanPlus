package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.DeleteHomeworkTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.MarkSingleDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.UpdateDueDateUseCase

data class HomeworkDetailUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getHomeworkByIdUseCase: GetHomeworkByIdUseCase,
    val taskDoneUseCase: MarkSingleDoneUseCase,
    val updateDueDateUseCase: UpdateDueDateUseCase,
    val deleteHomeworkTaskUseCase: DeleteHomeworkTaskUseCase
)

package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.MarkSingleDoneUseCase

data class HomeworkDetailUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getHomeworkByIdUseCase: GetHomeworkByIdUseCase,
    val taskDoneUseCase: MarkSingleDoneUseCase
)

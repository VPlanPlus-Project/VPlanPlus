package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase

data class HomeworkDetailUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getHomeworkByIdUseCase: GetHomeworkByIdUseCase
)

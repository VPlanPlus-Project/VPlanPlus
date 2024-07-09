package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase

data class HomeworkListUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getHomeworkUseCase: GetHomeworkUseCase,
)

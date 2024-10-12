package es.jvbabi.vplanplus.feature.exams.domain.usecase.details

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase

data class ExamDetailsUseCases(
    val getExamUseCase: GetExamUseCase,
    val getCurrentProfileUseCase: GetCurrentProfileUseCase
)

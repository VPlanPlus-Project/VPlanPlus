package es.jvbabi.vplanplus.feature.exams.domain.usecase.details

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase

data class ExamDetailsUseCases(
    val getExamUseCase: GetExamUseCase,
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,

    val updateTitleUseCase: UpdateExamTitleUseCase,
    val updateDateUseCase: UpdateExamDateUseCase,
    val updateCategoryUseCase: UpdateExamCategoryUseCase,
    val updateExamDetailsUseCase: UpdateExamDetailsUseCase,
    val updateReminderDaysUseCase: UpdateExamReminderDaysUseCase,

    val deleteExamUseCase: DeleteExamUseCase
)

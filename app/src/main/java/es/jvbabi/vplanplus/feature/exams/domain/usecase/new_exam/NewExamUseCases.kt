package es.jvbabi.vplanplus.feature.exams.domain.usecase.new_exam

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsDeveloperModeEnabledUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.GetDefaultLessonsUseCase

data class NewExamUseCases(
    val getDefaultLessonsUseCase: GetDefaultLessonsUseCase,
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val isDeveloperModeEnabled: IsDeveloperModeEnabledUseCase,
    val getCurrentLessonsUseCase: GetCurrentLessonsUseCase,

    val saveExamUseCase: SaveExamUseCase
)

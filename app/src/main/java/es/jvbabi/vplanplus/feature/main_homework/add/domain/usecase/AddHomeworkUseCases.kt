package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.IsBalloonUseCase
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase

data class AddHomeworkUseCases(
    val getDefaultLessonsUseCase: GetDefaultLessonsUseCase,
    val saveHomeworkUseCase: SaveHomeworkUseCase,
    val isBalloonUseCase: IsBalloonUseCase,
    val setBalloonUseCase: SetBalloonUseCase,
)
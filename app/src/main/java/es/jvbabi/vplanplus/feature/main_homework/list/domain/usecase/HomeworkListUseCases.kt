package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsBalloonUseCase
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.UpdateHomeworkUseCase

data class HomeworkListUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getHomeworkUseCase: GetHomeworkUseCase,
    val deleteHomeworkUseCase: DeleteHomeworkUseCase,
    val toggleHomeworkHiddenStateUseCase: ToggleHomeworkHiddenStateUseCase,
    val toggleDoneStateUseCase: ToggleDoneStateUseCase,
    val updateHomeworkUseCase: UpdateHomeworkUseCase,

    val isBalloonUseCase: IsBalloonUseCase,
    val setBalloonUseCase: SetBalloonUseCase,

    val setHomeworkEnabledUseCase: SetHomeworkEnabledUseCase
)

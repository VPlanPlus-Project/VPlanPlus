package es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided

import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ToggleHomeworkHiddenStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.reminderscheduler.OnNdpFinishedUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.reminderscheduler.OnNdpStartedUseCase

data class NdpGuidedUseCases(
    val toggleTaskDoneStateUseCase: ChangeTaskDoneStateUseCase,
    val toggleHomeworkHiddenUseCase: ToggleHomeworkHiddenStateUseCase,

    val getExamsToGetRemindedUseCase: GetExamsToGetRemindedUseCase,

    val markExamRemindersAsViewedUseCase: MarkExamRemindersAsViewedUseCase,

    val onNdpStartedUseCase: OnNdpStartedUseCase,
    val onNdpFinishedUseCase: OnNdpFinishedUseCase
)

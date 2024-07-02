package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase

class MarkAllDoneUseCase(
    private val changeTaskDoneStateUseCase: ChangeTaskDoneStateUseCase
) {
    suspend operator fun invoke(homework: Homework, done: Boolean): Boolean {
        return homework.tasks.all {
            changeTaskDoneStateUseCase(it, done)
        }
    }
}
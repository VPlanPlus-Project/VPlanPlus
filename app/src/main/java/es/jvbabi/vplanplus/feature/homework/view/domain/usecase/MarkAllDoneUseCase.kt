package es.jvbabi.vplanplus.feature.homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository

class MarkAllDoneUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(homework: Homework, done: Boolean) {
        homework.tasks.map { task ->
            task.copy(done = done)
        }.forEach { task ->
            homeworkRepository.setTaskState(homework, task, done)
        }
    }
}
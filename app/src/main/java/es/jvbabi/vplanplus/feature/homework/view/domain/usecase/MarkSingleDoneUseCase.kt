package es.jvbabi.vplanplus.feature.homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository

class MarkSingleDoneUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(task: HomeworkTask, done: Boolean) {
        homeworkRepository.upsertTask(task.copy(done = done))
    }
}
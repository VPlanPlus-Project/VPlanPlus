package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class MarkSingleDoneUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(task: HomeworkTask, done: Boolean): HomeworkModificationResult {
        val homework = homeworkRepository.getHomeworkByTask(task)
        return homeworkRepository.setTaskState(homework, task, done)
    }
}
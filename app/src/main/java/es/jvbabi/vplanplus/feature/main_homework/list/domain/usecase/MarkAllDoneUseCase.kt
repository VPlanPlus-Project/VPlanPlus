package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class MarkAllDoneUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(homework: Homework, done: Boolean): HomeworkModificationResult {
        return homework.tasks.map { task ->
            task.copy(isDone = done)
        }
            .map { task ->
                homeworkRepository.setTaskState(homework, task, done)
            }.minByOrNull { it.ordinal }!!
    }
}
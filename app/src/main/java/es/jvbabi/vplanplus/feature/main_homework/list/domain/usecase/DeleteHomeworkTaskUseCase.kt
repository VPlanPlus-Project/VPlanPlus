package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class DeleteHomeworkTaskUseCase(
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(task: HomeworkTask): HomeworkModificationResult {
        return homeworkRepository.deleteTask(task)
    }
}
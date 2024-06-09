package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class EditTaskUseCase(
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(task: HomeworkTask, newContent: String): HomeworkModificationResult {
        return homeworkRepository.editTaskContent(
            task = task,
            newContent = newContent.trim()
        )
    }
}
package es.jvbabi.vplanplus.feature.homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository

class EditTaskUseCase(
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(task: HomeworkTask, newContent: String) {
        homeworkRepository.editTaskContent(
            task = task,
            newContent = newContent.trim()
        )
    }
}
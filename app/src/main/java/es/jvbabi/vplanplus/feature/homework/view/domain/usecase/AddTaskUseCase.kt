package es.jvbabi.vplanplus.feature.homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository

class AddTaskUseCase(
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(homework: Homework, task: String) {
        homeworkRepository.addNewTask(homework, task)
    }
}
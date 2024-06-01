package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.DeleteTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class HideHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(homework: Homework) {
        homeworkRepository.removeOrHideHomework(homework, DeleteTask.HIDE)
    }
}
package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class ToggleHomeworkHiddenStateUseCase(
    private val homeworkRepository: HomeworkRepository,
) {

    suspend operator fun invoke(homework: Homework.CloudHomework) {
        homeworkRepository.changeHomeworkVisibilityDb(homework, !homework.isHidden)
    }
}
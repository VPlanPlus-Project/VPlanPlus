package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class ToggleHomeworkHiddenStateUseCase(
    private val homeworkRepository: HomeworkRepository,
) {

    suspend operator fun invoke(homework: Homework.CloudHomework) {
        val actualHomework = homeworkRepository.getHomeworkById(homework.id.toInt()).first() as Homework.CloudHomework
        homeworkRepository.changeHomeworkVisibilityDb(actualHomework, !actualHomework.isHidden)
    }
}
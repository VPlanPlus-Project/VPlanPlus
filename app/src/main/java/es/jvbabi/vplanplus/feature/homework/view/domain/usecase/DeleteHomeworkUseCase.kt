package es.jvbabi.vplanplus.feature.homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository

class DeleteHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository
){

    suspend operator fun invoke(homework: Homework) {
        homeworkRepository.deleteOrHideHomework(homework)
    }
}
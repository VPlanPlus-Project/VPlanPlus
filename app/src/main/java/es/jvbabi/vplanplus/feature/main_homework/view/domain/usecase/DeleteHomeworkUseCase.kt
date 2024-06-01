package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.DeleteTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class DeleteHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository
){

    suspend operator fun invoke(homework: Homework): HomeworkModificationResult {
        return homeworkRepository.removeOrHideHomework(homework, DeleteTask.DELETE)
    }
}
package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class HideHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
) {

    suspend operator fun invoke(homework: CloudHomework) {
        homeworkRepository.editHidingStatusInDb(homework, !homework.isHidden)
    }
}
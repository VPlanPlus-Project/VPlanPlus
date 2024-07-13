package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class EditTaskUseCase(
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(profile: ClassProfile, taskCore: HomeworkTaskCore, newContent: String): Boolean {
        val vppId = profile.vppId
        val homework = homeworkRepository.getHomeworkById(taskCore.homeworkId).first()
        if (homework is HomeworkCore.CloudHomework && vppId != null) homeworkRepository.changeTaskContentCloud(vppId, taskCore, newContent).value ?: return false
        homeworkRepository.changeTaskContentDb(taskCore, newContent)
        return true
    }
}
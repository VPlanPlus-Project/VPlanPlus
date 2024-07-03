package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class EditTaskUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {

    suspend operator fun invoke(task: HomeworkTask, newContent: String): Boolean {
        val vppId = (getCurrentProfileUseCase().first() as? ClassProfile ?: return false).vppId
        val homework = homeworkRepository.getHomeworkById(task.homeworkId).first()
        if (homework is CloudHomework && vppId != null) homeworkRepository.changeTaskContentCloud(vppId, task, newContent).value ?: return false
        homeworkRepository.changeTaskContentDb(task, newContent)
        return true
    }
}
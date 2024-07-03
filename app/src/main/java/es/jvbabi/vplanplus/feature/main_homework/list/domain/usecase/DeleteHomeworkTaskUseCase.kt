package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class DeleteHomeworkTaskUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {

    suspend operator fun invoke(task: HomeworkTask): Boolean {
        val vppID = (getCurrentProfileUseCase().first() as? ClassProfile ?: return false).vppId
        val homework = homeworkRepository.getHomeworkById(task.homeworkId).first()
        if (homework is CloudHomework && vppID != null) {
            homeworkRepository.deleteTaskCloud(vppID, task).value ?: return false
        }
        homeworkRepository.deleteTaskDb(task)
        return true
    }
}
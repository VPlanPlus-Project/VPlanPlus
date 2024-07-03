package es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class ChangeTaskDoneStateUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(homeworkTask: HomeworkTask, done: Boolean): Boolean {
        if (homeworkTask.isDone == done) return true
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return false
        if (profile.vppId != null) {
            homeworkRepository.changeTaskStateCloud(profile.vppId, homeworkTask.id, done).value ?: return false
        }
        homeworkRepository.changeTaskStateDb(homeworkTask.id, done)
        return true
    }
}
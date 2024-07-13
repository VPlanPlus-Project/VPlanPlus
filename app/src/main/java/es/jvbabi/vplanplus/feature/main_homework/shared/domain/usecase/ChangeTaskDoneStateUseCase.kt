package es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class ChangeTaskDoneStateUseCase(
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(profile: ClassProfile, homeworkTask: HomeworkTaskDone, done: Boolean): Boolean {
        if (homeworkTask.isDone == done) return true
        if (profile.vppId != null) {
            homeworkRepository.changeTaskStateCloud(profile.vppId, homeworkTask.id, done).value ?: return false
        }
        homeworkRepository.changeTaskStateDb(profile, homeworkTask.id, done)
        return true
    }
}
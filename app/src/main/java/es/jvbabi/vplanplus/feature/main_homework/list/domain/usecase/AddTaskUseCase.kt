package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class AddTaskUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {

    suspend operator fun invoke(homework: Homework, task: String): Boolean {
        val content = task.trim()
        if (content.isBlank()) return false
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return false
        var taskId: Int? = null
        if (homework.id > 0 && profile.vppId != null) {
            taskId = homeworkRepository.uploadHomeworkTask(profile.vppId, homework.id.toInt(), content).value ?: return false
        }

        homeworkRepository.addHomeworkTaskToDb(homeworkId = homework.id.toInt(), content = content, taskId = taskId)
        return true
    }
}
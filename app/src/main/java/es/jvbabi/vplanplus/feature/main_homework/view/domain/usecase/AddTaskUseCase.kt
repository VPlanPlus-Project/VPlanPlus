package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class AddTaskUseCase(
    private val homeworkRepository: HomeworkRepository,
) {

    suspend operator fun invoke(personalizedHomework: PersonalizedHomework, task: String): Boolean {
        val homework = personalizedHomework.homework
        val profile = personalizedHomework.profile

        val content = task.trim()
        if (content.isBlank()) return false
        var taskId: Int? = null
        if (homework.id > 0 && profile.vppId != null) {
            taskId = homeworkRepository.addTaskCloud(profile.vppId, homework.id, content).value ?: return false
        }

        homeworkRepository.addTaskDb(homeworkId = homework.id, content = content, taskId = taskId)
        return true
    }
}
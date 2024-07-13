package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class DeleteHomeworkTaskUseCase(
    private val homeworkRepository: HomeworkRepository,
) {

    suspend operator fun invoke(personalizedHomework: PersonalizedHomework, task: HomeworkTaskCore): Boolean {
        val profile = personalizedHomework.profile
        val homework = personalizedHomework.homework
        if (homework is HomeworkCore.CloudHomework && profile.vppId != null) {
            homeworkRepository.deleteTaskCloud(profile.vppId, task).value ?: return false
        }
        homeworkRepository.deleteTaskDb(task)
        return true
    }
}
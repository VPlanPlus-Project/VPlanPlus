package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import androidx.compose.ui.util.fastAny
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class MarkHomeworkAsDoneUseCase(
    private val homeworkRepository: HomeworkRepository,
) {
    suspend operator fun invoke(personalizedHomework: PersonalizedHomework): Boolean {
        val homework = personalizedHomework.homework
        val profile = (personalizedHomework.profile as? ClassProfile) ?: return false
        if (homework is HomeworkCore.CloudHomework && profile.vppId != null) {
            personalizedHomework.tasks.filter { !it.isDone }.map { task ->
                homeworkRepository.changeTaskStateCloud(profile.vppId, task.id, true)
            }.fastAny { it.value == null }.let {
                if (it) return@let null
                Unit
            } ?: return false
        }
        personalizedHomework.tasks.forEach { task ->
            if (task.isDone) return@forEach
            homeworkRepository.changeTaskStateDb(profile, task.id, true)
        }
        return true
    }
}
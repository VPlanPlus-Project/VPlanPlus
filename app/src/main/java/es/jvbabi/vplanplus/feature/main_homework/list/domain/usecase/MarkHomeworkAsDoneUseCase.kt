package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import androidx.compose.ui.util.fastAny
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class MarkHomeworkAsDoneUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(homework: Homework): Boolean {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return false
        if (homework is Homework.CloudHomework && profile.vppId != null) {
            homework.tasks.filter { !it.isDone }.map { task ->
                homeworkRepository.changeTaskStateCloud(profile.vppId, task.id, true)
            }.fastAny { it.value == null }.let {
                if (it) return@let null
                Unit
            } ?: return false
        }
        homework.tasks.forEach { task ->
            if (task.isDone) return@forEach
            homeworkRepository.changeTaskStateDb(task.id, true)
        }
        return true
    }
}
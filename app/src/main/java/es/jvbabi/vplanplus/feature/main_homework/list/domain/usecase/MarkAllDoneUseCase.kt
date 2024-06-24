package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class MarkAllDoneUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(homework: Homework, done: Boolean): HomeworkModificationResult {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return HomeworkModificationResult.FAILED
        return homework.tasks.map { task ->
            task.copy(isDone = done)
        }
            .map { task ->
                homeworkRepository.setTaskState(profile, homework, task, done)
            }.minByOrNull { it.ordinal }!!
    }
}
package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import android.util.Log
import androidx.compose.ui.util.fastAny
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

/**
 * Use case to toggle the done state of an entire homework.
 * If all tasks are done, the homework will be marked as undone.
 * Otherwise, all tasks will be marked as done.
 */
class ToggleDoneStateUseCase(
    private val homeworkRepository: HomeworkRepository,
) {
    suspend operator fun invoke(personalizedHomework: PersonalizedHomework): Boolean {
        val homework = personalizedHomework.homework
        val profile = (personalizedHomework.profile as? ClassProfile) ?: return false
        val desiredState = personalizedHomework.tasks.any { !it.isDone }
        Log.d("ToggleDoneStateUseCase", "Toggling done state of homework ${homework.id} to $desiredState")
        if (homework is HomeworkCore.CloudHomework && profile.vppId != null) {
            personalizedHomework.tasks.filter { it.isDone != desiredState }.map { task ->
                homeworkRepository.changeTaskStateCloud(profile.vppId, task.id, desiredState)
            }.fastAny { it.value == null }.let {
                if (it) return@let null
                Unit
            } ?: return false
        }
        personalizedHomework.tasks.forEach { task ->
            if (task.isDone == desiredState) return@forEach
            homeworkRepository.changeTaskStateDb(profile, task.id, desiredState)
        }
        return true
    }
}
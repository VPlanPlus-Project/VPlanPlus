package es.jvbabi.vplanplus.feature.main_homework.view.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.HomeworkDetailUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeworkDetailViewModel @Inject constructor(
    private val homeworkDetailUseCases: HomeworkDetailUseCases
): ViewModel() {

    var state by mutableStateOf(HomeworkDetailState())

    fun init(homeworkId: Int, onBack: () -> Unit) {
        viewModelScope.launch {
            combine(
                listOf(
                    homeworkDetailUseCases.getCurrentIdentityUseCase(),
                    homeworkDetailUseCases.getHomeworkByIdUseCase(homeworkId)
                )
            ) { data ->
                val identity = data[0] as Identity
                val homework = data[1] as Homework? ?: run { onBack(); return@combine state }
                state.copy(
                    homework = homework,
                    currentIdentity = identity,
                    canEdit = homework.canBeEdited(identity.profile!!)
                )
            }.collect {
                state = it
            }
        }
    }

    fun onAction(action: UiAction) {
        viewModelScope.launch {
            when (action) {
                is TaskDoneStateToggledAction -> homeworkDetailUseCases.taskDoneUseCase(action.homeworkTask, !action.homeworkTask.done)
                is ToggleEditModeAction -> state = state.copy(isEditing = !state.isEditing)
                is UpdateDueDateAction -> homeworkDetailUseCases.updateDueDateUseCase(state.homework!!, action.date)
                is DeleteTaskAction -> homeworkDetailUseCases.deleteHomeworkTaskUseCase(action.homeworkTask)
                is UpdateTaskContentAction -> homeworkDetailUseCases.editTaskUseCase(action.homeworkTask, action.content)
                is AddTaskAction -> homeworkDetailUseCases.addTaskUseCase(state.homework!!, action.content)
            }
        }
    }
}

data class HomeworkDetailState(
    val homework: Homework? = null,
    val currentIdentity: Identity? = null,
    val isEditing: Boolean = false,
    val canEdit: Boolean = false
)

sealed class UiAction

data class TaskDoneStateToggledAction(val homeworkTask: HomeworkTask) : UiAction()
data object ToggleEditModeAction: UiAction()
data class UpdateDueDateAction(val date: LocalDate): UiAction()
data class DeleteTaskAction(val homeworkTask: HomeworkTask): UiAction()
data class UpdateTaskContentAction(val homeworkTask: HomeworkTask, val content: String): UiAction()
data class AddTaskAction(val content: String): UiAction()
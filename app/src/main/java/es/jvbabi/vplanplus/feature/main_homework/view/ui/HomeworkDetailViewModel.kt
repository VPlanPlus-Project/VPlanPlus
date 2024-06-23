package es.jvbabi.vplanplus.feature.main_homework.view.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
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
                    homeworkDetailUseCases.getCurrentProfileUseCase(),
                    homeworkDetailUseCases.getHomeworkByIdUseCase(homeworkId)
                )
            ) { data ->
                val profile = data[0] as Profile?
                val homework = data[1] as Homework? ?: run { onBack(); return@combine state }
                state.copy(
                    homework = homework,
                    currentProfile = profile,
                    editTasks = homework.tasks.map { EditTask(it.id, it.content) },
                    canEdit = (profile as? ClassProfile)?.let { homework.canBeEdited(profile) } ?: false
                )
            }.collect {
                state = it
            }
        }
    }

    private fun initEditMode() {
        state = state.copy(
            isEditing = true,
            editDueDate = state.homework?.until?.toLocalDate(),
            editTasks = state.homework?.tasks.orEmpty().map { EditTask(it.id, it.content) },
            newTasks = emptyList(),
            hasEdited = false
        )
    }

    fun onAction(action: UiAction) {
        viewModelScope.launch {
            when (action) {
                is TaskDoneStateToggledAction -> homeworkDetailUseCases.taskDoneUseCase(action.homeworkTask, !action.homeworkTask.isDone)
                is StartEditModeAction -> initEditMode()
                is ExitEditModeAction -> {
                    if (action is ExitAndSaveHomeworkAction) {
                        if (state.editDueDate != null) homeworkDetailUseCases.updateDueDateUseCase(state.homework!!, state.editDueDate!!)

                        val editTasks = state.editTasks

                        val tasksToUpdate = editTasks.filter { state.homework?.getTaskById(it.id) != null && state.homework?.getTaskById(it.id)?.content != it.content }
                        tasksToUpdate.forEach { homeworkDetailUseCases.editTaskUseCase(state.homework!!.getTaskById(it.id)!!, it.content) }

                        state.newTasks.forEach { newTask -> homeworkDetailUseCases.addTaskUseCase(state.homework!!, newTask.content) }

                        val tasksToDelete = state.homework?.tasks?.filter { task -> editTasks.none { it.id == task.id } } ?: emptyList()
                        tasksToDelete.forEach { homeworkDetailUseCases.deleteHomeworkTaskUseCase(it) }
                    }
                    state = state.copy(
                        isEditing = false,
                        editDueDate = null,
                        editTasks = state.homework?.tasks.orEmpty().map { EditTask(it.id, it.content) },
                        newTasks = emptyList()
                    )
                }
                is UpdateDueDateAction -> state = state.copy(editDueDate = action.date, hasEdited = true)
                is AddTaskAction -> {
                    val newTask = EditTask(id = action.newTaskId, content = action.content)
                    state = state.copy(newTasks = state.newTasks + newTask, hasEdited = true)
                }
                is DeleteExistingTaskAction -> state = state.copy(editTasks = state.editTasks.filter { it.id != action.existingTaskId }, hasEdited = true)
                is DeleteNewTaskAction -> state = state.copy(newTasks = state.newTasks.filter { it.id != action.newTaskId })
                is UpdateTaskContentAction -> {
                    if (action is UpdateExistingTaskContentAction) {
                        state = state.copy(editTasks = state.editTasks.map {
                            if (it.id == action.existingTaskId) it.copy(content = action.content) else it
                        })
                    } else if (action is UpdateNewTaskContentAction) {
                        state = state.copy(newTasks = state.newTasks.map {
                            if (it.id == action.newTaskId) it.copy(content = action.content) else it
                        })
                    }
                    state = state.copy(hasEdited = true)
                }
            }
        }
    }
}

data class HomeworkDetailState(
    val homework: Homework? = null,
    val currentProfile: Profile? = null,
    val canEdit: Boolean = false,

    val isEditing: Boolean = false,
    val hasEdited: Boolean = false,
    val editDueDate: LocalDate? = null,
    val editTasks: List<EditTask> = emptyList(),
    val newTasks: List<EditTask> = emptyList(),
)

data class EditTask(
    val id: Long,
    val content: String,
)

sealed class UiAction

data class TaskDoneStateToggledAction(val homeworkTask: HomeworkTask) : UiAction()
data object StartEditModeAction: UiAction()
sealed class ExitEditModeAction: UiAction()
data object ExitAndSaveHomeworkAction: ExitEditModeAction()
data object ExitAndDiscardChangesAction: ExitEditModeAction()
data class UpdateDueDateAction(val date: LocalDate): UiAction()
data class DeleteNewTaskAction(val newTaskId: Long): UiAction()

data class DeleteExistingTaskAction(val existingTaskId: Long): UiAction()
sealed class UpdateTaskContentAction(val content: String): UiAction()
data class UpdateExistingTaskContentAction(val existingTaskId: Long, val description: String) : UpdateTaskContentAction(description)

data class UpdateNewTaskContentAction(val newTaskId: Long, val description: String) : UpdateTaskContentAction(description)
data class AddTaskAction(val newTaskId: Long, val content: String): UiAction()

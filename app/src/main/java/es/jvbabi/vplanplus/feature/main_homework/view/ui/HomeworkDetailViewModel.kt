package es.jvbabi.vplanplus.feature.main_homework.view.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.LocalHomework
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.DocumentUpdate
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.HomeworkDetailUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeworkDetailViewModel @Inject constructor(
    private val homeworkDetailUseCases: HomeworkDetailUseCases
) : ViewModel() {

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
                    canEditOrigin = (profile as? ClassProfile)?.let { homework is LocalHomework || (homework is CloudHomework && homework.createdBy == it.vppId) } ?: false
                )
            }.collect {
                state = it
            }
        }
    }

    private fun initEditMode(editing: Boolean = true) {
        state = state.copy(
            isEditing = editing,
            editDueDate = state.homework?.until?.toLocalDate(),
            newTasks = emptyList(),
            editedTasks = emptyList(),
            tasksToDelete = emptyList(),
            newDocuments = emptyMap(),
            editedDocuments = emptyList(),
            documentsToDelete = emptyList(),
            hasEdited = false,
            editVisibility = null
        )
    }

    fun onAction(action: UiAction) {
        viewModelScope.launch {
            when (action) {
                is TaskDoneStateToggledAction -> homeworkDetailUseCases.taskDoneUseCase(action.homeworkTask, !action.homeworkTask.isDone)
                is StartEditModeAction -> initEditMode()
                is ExitEditModeAction -> {
                    if (action is ExitAndSaveHomeworkAction) {
                        state = state.copy(isLoading = true)
                        val homework = state.homework
                        val editVisibility = state.editVisibility
                        if (state.editDueDate != null) homeworkDetailUseCases.updateDueDateUseCase(state.homework!!, state.editDueDate!!)
                        if (editVisibility != null && homework is CloudHomework) homeworkDetailUseCases.updateHomeworkVisibilityUseCase(homework, editVisibility)

                        val changeDocuments = state.documentsToDelete.isNotEmpty() || state.newDocuments.isNotEmpty() || state.editedDocuments.isNotEmpty()
                        if (changeDocuments) homeworkDetailUseCases.updateDocumentsUseCase(
                            homework = state.homework!!,
                            newDocuments = state.newDocuments.keys,
                            editedDocuments = state.editedDocuments,
                            documentsToDelete = state.documentsToDelete,
                            onUploading = { uri, progress -> state = state.copy(newDocuments = state.newDocuments + (state.newDocuments.keys.first { it.uri == uri } to progress)) }
                        )

                        state.editedTasks.forEach { homeworkDetailUseCases.editTaskUseCase(state.homework!!.getTaskById(it.id)!!, it.content) }
                        state.newTasks.forEach { newTask -> homeworkDetailUseCases.addTaskUseCase(state.homework!!, newTask.content) }
                        state.tasksToDelete.forEach { homeworkDetailUseCases.deleteHomeworkTaskUseCase(it) }
                        state = state.copy(isLoading = false)
                    }
                    initEditMode(false)
                }

                is UpdateDueDateAction -> state = state.copy(editDueDate = action.date, hasEdited = true)
                is ChangeVisibilityAction -> state = state.copy(editVisibility = action.isPublicOrVisible, hasEdited = true)
                is AddTaskAction -> {
                    val newTask = NewTask(content = action.newTask.content)
                    state = state.copy(newTasks = state.newTasks + newTask, hasEdited = true)
                }

                is DeleteTaskAction -> {
                    state = when (action.task) {
                        is NewTask -> state.copy(newTasks = state.newTasks.filter { it.id != action.task.id }, hasEdited = true)
                        is EditedTask -> state.copy(tasksToDelete = state.tasksToDelete + state.homework!!.getTaskById(action.task.id)!!, hasEdited = true)
                    }
                }

                is UpdateTaskContentAction -> {
                    state = when (action.task) {
                        is NewTask -> {
                            if (state.newTasks.none { it.id == action.task.id }) state.copy(newTasks = state.newTasks + action.task, hasEdited = true)
                            else state.copy(newTasks = state.newTasks.map { if (it.id == action.task.id) action.task else it }, hasEdited = true)
                        }

                        is EditedTask -> {
                            if (state.editedTasks.none { it.id == action.task.id }) state.copy(editedTasks = state.editedTasks + action.task, hasEdited = true)
                            else state.copy(editedTasks = state.editedTasks.map { if (it.id == action.task.id) action.task else it }, hasEdited = true)
                        }
                    }
                }

                is AddDocumentAction -> {
                    val newDocument = DocumentUpdate.NewDocument(action.newDocument.uri, extension = action.newDocument.extension)
                    state = state.copy(newDocuments = state.newDocuments + (newDocument to null), hasEdited = true)
                }

                is RenameDocumentAction -> {
                    state = when (action.document) {
                        is DocumentUpdate.NewDocument -> state.copy(newDocuments = state.newDocuments + (action.document to null), hasEdited = true)
                        is DocumentUpdate.EditedDocument -> {
                            if (state.editedDocuments.none { it.uri == action.document.uri }) state.copy(editedDocuments = state.editedDocuments + action.document, hasEdited = true)
                            else state.copy(editedDocuments = state.editedDocuments.map { if (it.uri == action.document.uri) action.document else it }, hasEdited = true)
                        }
                    }
                }

                is DeleteDocumentAction -> {
                    state = when (action.document) {
                        is DocumentUpdate.NewDocument -> state.copy(newDocuments = state.newDocuments.filter { it.key.uri != action.document.uri }, hasEdited = true)
                        is DocumentUpdate.EditedDocument -> {
                            state.copy(
                                documentsToDelete = state.documentsToDelete + state.homework!!.documents.first { it.documentId == action.document.documentId },
                                editedDocuments = state.editedDocuments.filter { it.uri != action.document.uri },
                                hasEdited = true
                            )
                        }
                    }
                }
            }
        }
    }
}

data class HomeworkDetailState(
    val homework: Homework? = null,
    val currentProfile: Profile? = null,
    val canEditOrigin: Boolean = false,


    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val hasEdited: Boolean = false,
    val editDueDate: LocalDate? = null,
    /**
     * Null if nothing should be changed, true if the homework should become public or visible, false if it should be hidden or private
     */
    val editVisibility: Boolean? = null,

    val tasksToDelete: List<HomeworkTask> = emptyList(),
    val newTasks: List<NewTask> = emptyList(),
    val editedTasks: List<EditedTask> = emptyList(),

    val documentsToDelete: List<HomeworkDocument> = emptyList(),
    val newDocuments: Map<DocumentUpdate.NewDocument, Float?> = emptyMap(), // Document to Upload progress
    val editedDocuments: List<DocumentUpdate.EditedDocument> = emptyList()
)

sealed class UiAction

data class TaskDoneStateToggledAction(val homeworkTask: HomeworkTask) : UiAction()
data object StartEditModeAction : UiAction()
sealed class ExitEditModeAction : UiAction()
data object ExitAndSaveHomeworkAction : ExitEditModeAction()
data object ExitAndDiscardChangesAction : ExitEditModeAction()
data class UpdateDueDateAction(val date: LocalDate) : UiAction()
data class ChangeVisibilityAction(val isPublicOrVisible: Boolean) : UiAction()
data class DeleteTaskAction(val task: TaskUpdate) : UiAction()
data class UpdateTaskContentAction(val task: TaskUpdate) : UiAction()

data class AddDocumentAction(val newDocument: DocumentUpdate.NewDocument) : UiAction()
data class RenameDocumentAction(val document: DocumentUpdate) : UiAction()
data class DeleteDocumentAction(val document: DocumentUpdate) : UiAction()

data class AddTaskAction(val newTask: NewTask) : UiAction()

sealed class TaskUpdate(val content: String)

class NewTask(
    val id: UUID = UUID.randomUUID(),
    content: String
) : TaskUpdate(content)

class EditedTask(
    val id: Long,
    content: String
) : TaskUpdate(content)


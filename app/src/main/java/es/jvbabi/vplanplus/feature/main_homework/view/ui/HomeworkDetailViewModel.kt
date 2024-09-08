package es.jvbabi.vplanplus.feature.main_homework.view.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
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
                    homeworkDetailUseCases.getHomeworkByIdUseCase(homeworkId)
                )
            ) { data ->
                val personalizedHomework = data[0] ?: run { onBack(); return@combine state }
                state.copy(
                    personalizedHomework = personalizedHomework,
                    documents = personalizedHomework.homework.documents.map { HomeworkDocumentUi(it) }
                )
            }.collect {
                state = it
            }
        }
    }

    private fun initEditMode(editing: Boolean = true) {
        state = state.copy(
            isEditing = editing,
            editDueDate = state.personalizedHomework?.homework?.until?.toLocalDate(),
            newTasks = emptyList(),
            editedTasks = emptyList(),
            tasksToDelete = emptyList(),
            newDocuments = emptyList(),
            editedDocuments = emptyList(),
            documentsToDelete = emptyList(),
            hasEdited = false,
            editVisibility = null
        )
    }

    fun onAction(action: UiAction) {
        viewModelScope.launch {
            when (action) {
                is TaskDoneStateToggledAction -> homeworkDetailUseCases.taskDoneUseCase(state.personalizedHomework!!.profile, action.homeworkTask, !action.homeworkTask.isDone)
                is StartEditModeAction -> initEditMode()
                is ExitEditModeAction -> {
                    if (action is ExitAndSaveHomeworkAction) {
                        state = state.copy(isLoading = true)
                        val homework = state.personalizedHomework
                        val editVisibility = state.editVisibility
                        if (state.editDueDate != null) homeworkDetailUseCases.updateDueDateUseCase(state.personalizedHomework!!, state.editDueDate!!)
                        if (editVisibility != null && homework is PersonalizedHomework.CloudHomework) homeworkDetailUseCases.updateHomeworkVisibilityUseCase(homework, editVisibility)

                        val changeDocuments = state.documentsToDelete.isNotEmpty() || state.newDocuments.isNotEmpty() || state.editedDocuments.isNotEmpty()
                        if (changeDocuments) homeworkDetailUseCases.updateDocumentsUseCase(
                            personalizedHomework = state.personalizedHomework!!,
                            newDocuments = state.newDocuments,
                            editedDocuments = state.editedDocuments,
                            documentsToDelete = state.documentsToDelete,
                            onUploading = { uri, progress -> state = state.copy(newDocuments = state.newDocuments.map { if (it.uri == uri) it.copy(progress = progress) else it }) }
                        )

                        state.editedTasks.forEach { homeworkDetailUseCases.editTaskUseCase(state.personalizedHomework!!.profile, state.personalizedHomework!!.homework.getTaskById(it.id.toInt())!!, it.content) }
                        state.newTasks.forEach { newTask -> homeworkDetailUseCases.addTaskUseCase(state.personalizedHomework!!, newTask.content) }
                        state.tasksToDelete.forEach { homeworkDetailUseCases.deleteHomeworkTaskUseCase(state.personalizedHomework!!, it) }
                        state = state.copy(isLoading = false)
                    }
                    initEditMode(false)
                }
                is DeleteHomework -> {
                    state = state.copy(isLoading = true)
                    homeworkDetailUseCases.deleteHomeworkUseCase(state.personalizedHomework!!)
                    state = state.copy(isLoading = false)
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
                        is EditedTask -> state.copy(tasksToDelete = state.tasksToDelete + state.personalizedHomework!!.getTaskById(action.task.id.toInt())!!, hasEdited = true)
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
                    val newDocument = DocumentUpdate.NewDocument(action.newDocument.uri, size = action.newDocument.size, extension = action.newDocument.extension)
                    state = state.copy(newDocuments = state.newDocuments + newDocument, hasEdited = true)
                }

                is RenameDocumentAction -> {
                    state = when (action.document) {
                        is DocumentUpdate.NewDocument -> {
                            val newDocumentItem = state.newDocuments.toList().indexOfFirst { it.uri == action.document.uri }
                            val newList = if(newDocumentItem == -1) state.newDocuments + action.document else state.newDocuments.toList().mapIndexed { index, pair -> if (index == newDocumentItem) action.document else pair }
                            state.copy(newDocuments = newList, hasEdited = true)
                        }
                        is DocumentUpdate.EditedDocument -> {
                            if (state.editedDocuments.none { it.uri == action.document.uri }) state.copy(editedDocuments = state.editedDocuments + action.document, hasEdited = true)
                            else state.copy(editedDocuments = state.editedDocuments.map { if (it.uri == action.document.uri) action.document else it }, hasEdited = true)
                        }
                    }
                }

                is DeleteDocumentAction -> {
                    state = when (action.document) {
                        is DocumentUpdate.NewDocument -> state.copy(newDocuments = state.newDocuments.filter { it.uri != action.document.uri }, hasEdited = true)
                        is DocumentUpdate.EditedDocument -> {
                            state.copy(
                                documentsToDelete = state.documentsToDelete + state.personalizedHomework!!.homework.documents.first { it.documentId == action.document.documentId },
                                editedDocuments = state.editedDocuments.filter { it.uri != action.document.uri },
                                hasEdited = true
                            )
                        }
                    }
                }

                is DownloadDocumentAction -> {
                    homeworkDetailUseCases.downloadDocumentUseCase(state.personalizedHomework!!, action.document, onDownloading = { sent, total ->
                        state = state.copy(documents = state.documents.map { if (it.document.documentId == action.document.documentId) it.copy(progress = sent.toFloat() / total.toFloat()) else it })
                    })
                    state = state.copy(documents = state.documents.map { if (it.document.documentId == action.document.documentId) it.copy(progress = null) else it })
                }
            }
        }
    }
}

data class HomeworkDetailState(
    val personalizedHomework: PersonalizedHomework? = null,
    val documents: List<HomeworkDocumentUi> = emptyList(),

    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val hasEdited: Boolean = false,
    val editDueDate: LocalDate? = null,
    /**
     * Null if nothing should be changed, true if the homework should become public or visible, false if it should be hidden or private
     */
    val editVisibility: Boolean? = null,

    val tasksToDelete: List<HomeworkTaskDone> = emptyList(),
    val newTasks: List<NewTask> = emptyList(),
    val editedTasks: List<EditedTask> = emptyList(),

    val documentsToDelete: List<HomeworkDocument> = emptyList(),
    val newDocuments: List<DocumentUpdate.NewDocument> = emptyList(),
    val editedDocuments: List<DocumentUpdate.EditedDocument> = emptyList()
) {
    val canEditOrigin: Boolean
        get() = personalizedHomework is PersonalizedHomework.LocalHomework || personalizedHomework is PersonalizedHomework.CloudHomework && personalizedHomework.homework.createdBy == personalizedHomework.profile.vppId
}

sealed class UiAction

data class TaskDoneStateToggledAction(val homeworkTask: HomeworkTaskDone) : UiAction()
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

data class DownloadDocumentAction(val document: HomeworkDocument) : UiAction()

data class AddTaskAction(val newTask: NewTask) : UiAction()

data object DeleteHomework : UiAction()

sealed class TaskUpdate(val content: String)

class NewTask(
    val id: UUID = UUID.randomUUID(),
    content: String
) : TaskUpdate(content)

class EditedTask(
    val id: Long,
    content: String
) : TaskUpdate(content)

data class HomeworkDocumentUi(
    val document: HomeworkDocument,
    val progress: Float? = null,
)
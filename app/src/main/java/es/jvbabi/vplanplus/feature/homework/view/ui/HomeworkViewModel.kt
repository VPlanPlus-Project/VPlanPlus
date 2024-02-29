package es.jvbabi.vplanplus.feature.homework.view.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.HomeworkUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val homeworkUseCases: HomeworkUseCases,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
): ViewModel() {

    val state = mutableStateOf(HomeworkState())

    init {
        viewModelScope.launch {
            combine(
                homeworkUseCases.getHomeworkUseCase(),
                getCurrentIdentityUseCase()
            ) { homework, identity ->
                state.value.copy(
                    homework = homework.homework.map { HomeworkViewModelRecord(it, it.createdBy?.id == identity?.vppId?.id || it.createdBy == null) },
                    wrongProfile = homework.wrongProfile,
                    identity = identity ?: Identity()
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun markAllDone(homework: Homework, done: Boolean) {
        viewModelScope.launch {
            if (homeworkUseCases.markAllDoneUseCase(homework, done) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.CHANGE_HOMEWORK_STATE to done,
                    errorVisible = true
                )
            }
        }
    }

    fun markSingleDone(homeworkTask: HomeworkTask, done: Boolean) {
        viewModelScope.launch {
            if (homeworkUseCases.markSingleDoneUseCase(homeworkTask, done) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.CHANGE_TASK_STATE to done,
                    errorVisible = true
                )
            }
        }
    }

    fun onAddTask(homework: Homework, task: String) {
        viewModelScope.launch {
            if (homeworkUseCases.addTaskUseCase(homework, task) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.ADD_TASK to task,
                    errorVisible = true
                )
            }
        }
    }

    fun onHomeworkDeleteRequest(homework: Homework?) {
        state.value = state.value.copy(homeworkDeletionRequest = homework)
    }

    fun onHomeworkChangeVisibilityRequest(homework: Homework?) {
        state.value = state.value.copy(homeworkChangeVisibilityRequest = homework)
    }

    fun onConfirmHomeworkDeleteRequest() {
        state.value.homeworkDeletionRequest?.let {
            viewModelScope.launch {
                if (homeworkUseCases.deleteHomeworkUseCase(it) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.DELETE_HOMEWORK to null,
                        errorVisible = true
                    )
                }
                onHomeworkDeleteRequest(null)
            }
        }
    }

    fun onConfirmHomeworkChangeVisibilityRequest() {
        state.value.homeworkChangeVisibilityRequest?.let {
            viewModelScope.launch {
                if (homeworkUseCases.changeVisibilityUseCase(it) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.CHANGE_HOMEWORK_VISIBILITY to null,
                        errorVisible = true
                    )
                }
                onHomeworkChangeVisibilityRequest(null)
            }
        }
    }

    fun onHomeworkTaskDeleteRequest(homeworkTask: HomeworkTask?) {
        state.value = state.value.copy(homeworkTaskDeletionRequest = homeworkTask)
    }

    fun onHomeworkTaskDeleteRequestConfirm() {
        state.value.homeworkTaskDeletionRequest?.let {
            viewModelScope.launch {
                if (homeworkUseCases.deleteHomeworkTaskUseCase(it) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.DELETE_TASK to null,
                        errorVisible = true
                    )
                }
                onHomeworkTaskDeleteRequest(null)
            }
        }
    }

    fun onHomeworkTaskEditRequest(homeworkTask: HomeworkTask?) {
        state.value = state.value.copy(editHomeworkTask = homeworkTask)
    }

    fun onHomeworkTaskEditRequestConfirm(newContent: String?) {
        if (newContent == null) {
            onHomeworkTaskEditRequest(null)
            return
        }
        state.value.editHomeworkTask?.let {
            if (newContent == it.content) return@let
            if (newContent.isBlank()) return@let
            viewModelScope.launch {
                if (homeworkUseCases.editTaskUseCase(it, newContent) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.EDIT_TASK to newContent,
                        errorVisible = true
                    )
                }
                onHomeworkTaskEditRequest(null)
            }
        }
    }

    fun onResetError() {
        state.value = state.value.copy(errorVisible = false)
    }
}

data class HomeworkState(
    val homework: List<HomeworkViewModelRecord> = emptyList(),
    val wrongProfile: Boolean = false,
    val identity: Identity = Identity(),
    val homeworkDeletionRequest: Homework? = null,
    val homeworkChangeVisibilityRequest: Homework? = null,
    val homeworkTaskDeletionRequest: HomeworkTask? = null,
    val editHomeworkTask: HomeworkTask? = null,
    val errorResponse: Pair<ErrorOnUpdate, Any?>? = null,
    val errorVisible: Boolean = false
)

data class HomeworkViewModelRecord(
    val homework: Homework,
    val isOwner: Boolean
)

enum class ErrorOnUpdate {
    DELETE_TASK,
    EDIT_TASK,
    CHANGE_TASK_STATE,
    CHANGE_HOMEWORK_STATE,
    CHANGE_HOMEWORK_VISIBILITY,
    DELETE_HOMEWORK,
    ADD_TASK
}
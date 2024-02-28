package es.jvbabi.vplanplus.feature.homework.view.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
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
            homeworkUseCases.markAllDoneUseCase(homework, done)
        }
    }

    fun markSingleDone(homeworkTask: HomeworkTask, done: Boolean) {
        viewModelScope.launch {
            homeworkUseCases.markSingleDoneUseCase(homeworkTask, done)
        }
    }

    fun onAddTask(homework: Homework, task: String) {
        viewModelScope.launch {
            homeworkUseCases.addTaskUseCase(homework, task)
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
                homeworkUseCases.deleteHomeworkUseCase(it)
                onHomeworkDeleteRequest(null)
            }
        }
    }

    fun onConfirmHomeworkChangeVisibilityRequest() {
        state.value.homeworkChangeVisibilityRequest?.let {
            viewModelScope.launch {
                homeworkUseCases.changeVisibilityUseCase(it)
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
                homeworkUseCases.deleteHomeworkTaskUseCase(it)
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
                homeworkUseCases.editTaskUseCase(it, newContent)
                onHomeworkTaskEditRequest(null)
            }
        }
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
)

data class HomeworkViewModelRecord(
    val homework: Homework,
    val isOwner: Boolean
)
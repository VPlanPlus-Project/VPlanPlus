package es.jvbabi.vplanplus.feature.homework.view.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.HomeworkUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
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
                    homework = homework
                        .homework
                        .map { it.toViewModel(it.createdBy?.id == identity?.vppId?.id) },
                    wrongProfile = homework.wrongProfile,
                    identity = identity ?: Identity()
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun markAllDone(homework: HomeworkViewModelHomework, done: Boolean) {
        viewModelScope.launch {
            setHomeworkLoading(homework.id, true)
            if (homeworkUseCases.markAllDoneUseCase(homework.toHomework(), done) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.CHANGE_HOMEWORK_STATE to done,
                    errorVisible = true
                )
            }
            setHomeworkLoading(homework.id, false)
        }
    }

    fun markSingleDone(homeworkTask: HomeworkViewModelTask, done: Boolean) {
        viewModelScope.launch {
            setTaskLoading(homeworkTask.id, true)
            if (homeworkUseCases.markSingleDoneUseCase(homeworkTask.toTask(), done) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.CHANGE_TASK_STATE to done,
                    errorVisible = true
                )
            }
            setTaskLoading(homeworkTask.id, false)
        }
    }

    fun onAddTask(homework: HomeworkViewModelHomework, task: String) {
        viewModelScope.launch {
            if (homeworkUseCases.addTaskUseCase(homework.toHomework(), task) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.ADD_TASK to task,
                    errorVisible = true
                )
            }
        }
    }

    fun onHomeworkDeleteRequest(homework: HomeworkViewModelHomework?) {
        state.value = state.value.copy(homeworkDeletionRequest = homework?.toHomework())
    }

    fun onHomeworkChangeVisibilityRequest(homework: HomeworkViewModelHomework?) {
        state.value = state.value.copy(homeworkChangeVisibilityRequest = homework?.toHomework())
    }

    fun onConfirmHomeworkDeleteRequest() {
        state.value.homeworkDeletionRequest?.let {
            viewModelScope.launch {
                setHomeworkLoading(it.id, true)
                onHomeworkDeleteRequest(null)
                if (homeworkUseCases.deleteHomeworkUseCase(it) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.DELETE_HOMEWORK to null,
                        errorVisible = true
                    )
                }
                setHomeworkLoading(it.id, false)
            }
        }
    }

    fun onConfirmHomeworkChangeVisibilityRequest() {
        state.value.homeworkChangeVisibilityRequest?.let {
            viewModelScope.launch {
                setHomeworkLoading(it.id, true)
                onHomeworkChangeVisibilityRequest(null)
                if (homeworkUseCases.changeVisibilityUseCase(it) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.CHANGE_HOMEWORK_VISIBILITY to null,
                        errorVisible = true
                    )
                }
                setHomeworkLoading(it.id, false)
            }
        }
    }

    fun onHomeworkTaskDeleteRequest(homeworkTask: HomeworkViewModelTask?) {
        state.value = state.value.copy(homeworkTaskDeletionRequest = homeworkTask?.toTask())
    }

    fun onHomeworkTaskDeleteRequestConfirm() {
        val homeworkTask = state.value.homeworkTaskDeletionRequest ?: return
        viewModelScope.launch {
            setTaskLoading(homeworkTask.id, true)
            onHomeworkTaskDeleteRequest(null)
            if (homeworkUseCases.deleteHomeworkTaskUseCase(homeworkTask) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.DELETE_TASK to null,
                    errorVisible = true
                )
                setTaskLoading(homeworkTask.id, false)
            }
        }
    }

    private fun setTaskLoading(taskId: Long, loading: Boolean) {
        state.value = state.value.copy(
            homework = state.value.homework.map {
                it.copy(
                    tasks = it.tasks.map { task ->
                        if (task.id == taskId) task.copy(isLoading = loading)
                        else task
                    }
                )
            }
        )
    }

    private fun setHomeworkLoading(homeworkId: Long, loading: Boolean) {
        state.value = state.value.copy(
            homework = state.value.homework.map {
                if (it.id == homeworkId) it.copy(isLoading = loading)
                else it
            }
        )
    }

    fun onHomeworkTaskEditRequest(homeworkTask: HomeworkViewModelTask?) {
        state.value = state.value.copy(editHomeworkTask = homeworkTask?.toTask())
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
                setTaskLoading(it.id, true)
                onHomeworkTaskEditRequest(null)
                if (homeworkUseCases.editTaskUseCase(it, newContent) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.EDIT_TASK to newContent,
                        errorVisible = true
                    )
                }
                setTaskLoading(it.id, false)
            }
        }
    }

    fun onResetError() {
        state.value = state.value.copy(errorVisible = false)
    }
}

data class HomeworkState(
    val homework: List<HomeworkViewModelHomework> = emptyList(),
    val wrongProfile: Boolean = false,
    val identity: Identity = Identity(),
    val homeworkDeletionRequest: Homework? = null,
    val homeworkChangeVisibilityRequest: Homework? = null,
    val homeworkTaskDeletionRequest: HomeworkTask? = null,
    val editHomeworkTask: HomeworkTask? = null,
    val errorResponse: Pair<ErrorOnUpdate, Any?>? = null,
    val errorVisible: Boolean = false
)

data class HomeworkViewModelHomework(
    val id: Long,
    val createdBy: VppId?,
    val classes: Classes,
    val createdAt: LocalDateTime,
    val defaultLesson: DefaultLesson,
    val isPublic: Boolean,
    val until: LocalDate,
    val tasks: List<HomeworkViewModelTask>,
    val isOwner: Boolean,
    val isLoading: Boolean = false
) {
    fun toHomework() = Homework(
        id = id,
        createdBy = createdBy,
        classes = classes,
        createdAt = createdAt,
        defaultLesson = defaultLesson,
        isPublic = isPublic,
        until = until,
        tasks = tasks.map { it.toTask() }
    )

}

data class HomeworkViewModelTask(
    val id: Long,
    val individualId: Long?,
    val content: String,
    val done: Boolean,
    val isLoading: Boolean = false
) {
    fun toTask() = HomeworkTask(id, individualId, content, done)
}

private fun Homework.toViewModel(isOwner: Boolean) = HomeworkViewModelHomework(
    id = id,
    createdBy = createdBy,
    classes = classes,
    createdAt = createdAt,
    defaultLesson = defaultLesson,
    isPublic = isPublic,
    until = until,
    tasks = tasks.map { it.toViewModel() },
    isOwner = isOwner
)

private fun HomeworkTask.toViewModel() = HomeworkViewModelTask(
    id = id,
    individualId = individualId,
    content = content,
    done = done
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
package es.jvbabi.vplanplus.feature.main_homework.list.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.HomeworkResult
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.HomeworkUseCases
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val homeworkUseCases: HomeworkUseCases,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) : ViewModel() {

    val state = mutableStateOf(HomeworkState())

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    homeworkUseCases.getHomeworkUseCase(),
                    homeworkUseCases.isUpdateRunningUseCase(),
                    getCurrentProfileUseCase(),
                    homeworkUseCases.showHomeworkNotificationBannerUseCase()
                )
            ) { data ->
                val homework = data[0] as HomeworkResult
                val isUpdateRunning = data[1] as Boolean
                val profile = data[2] as Profile?
                val showNotificationBanner = data[3] as Boolean

                state.value.copy(
                    homework = homework.homework,
                    wrongProfile = homework.wrongProfile,
                    profile = profile,
                    isUpdating = isUpdateRunning,
                    showNotificationBanner = showNotificationBanner
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun markAllDone(homework: Homework, done: Boolean) {
        viewModelScope.launch {
            setHomeworkLoading(homework, true)
            if (homeworkUseCases.markAllDoneUseCase(homework, done).not()) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.CHANGE_HOMEWORK_STATE to done,
                    errorVisible = true
                )
            }
        }
    }

    fun markSingleDone(homeworkTask: HomeworkTask, done: Boolean) {
        val homework = state.value.homework.find { it.tasks.any { task -> task.id == homeworkTask.id } } ?: return
        viewModelScope.launch {
            setHomeworkLoading(homework, true)
            if (homeworkUseCases.markSingleDoneUseCase(homeworkTask, done).not()) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.CHANGE_TASK_STATE to done,
                    errorVisible = true
                )
            }
            setHomeworkLoading(homework, false)
        }
    }

    private fun setHomeworkLoading(homework: Homework, isLoading: Boolean) {
        if (isLoading) state.value = state.value.copy(loadingHomeworkIds = state.value.loadingHomeworkIds + homework.id.toInt())
        else state.value = state.value.copy(loadingHomeworkIds = state.value.loadingHomeworkIds - homework.id.toInt())
    }

    fun onAddTask(homework: Homework, task: String) {
        viewModelScope.launch {
            setHomeworkLoading(homework, true)
            if (homeworkUseCases.addTaskUseCase(homework, task).not()) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.ADD_TASK to task,
                    errorVisible = true
                )
            }
            setHomeworkLoading(homework, false)
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
                setHomeworkLoading(it, true)
                onHomeworkDeleteRequest(null)
                if (homeworkUseCases.deleteHomeworkUseCase(it) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.DELETE_HOMEWORK to null,
                        errorVisible = true
                    )
                    setHomeworkLoading(it, false)
                }
            }
        }
    }

    fun onConfirmHomeworkChangeVisibilityRequest() {
        state.value.homeworkChangeVisibilityRequest?.let {
            viewModelScope.launch {
                if (it !is CloudHomework) return@launch
                setHomeworkLoading(it, true)
                onHomeworkChangeVisibilityRequest(null)
                if (homeworkUseCases.changeVisibilityUseCase(it) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.CHANGE_HOMEWORK_VISIBILITY to null,
                        errorVisible = true
                    )
                    setHomeworkLoading(it, false)
                }
            }
        }
    }

    fun onHomeworkTaskDeleteRequest(homeworkTask: HomeworkTask?) {
        state.value = state.value.copy(homeworkTaskDeletionRequest = homeworkTask)
    }

    fun onHomeworkTaskDeleteRequestConfirm() {
        val homeworkTask = state.value.homeworkTaskDeletionRequest ?: return
        val homework = state.value.homework.find { it.tasks.any { task -> task.id == homeworkTask.id } } ?: return
        viewModelScope.launch {
            setHomeworkLoading(homework, true)
            onHomeworkTaskDeleteRequest(null)
            if (homeworkUseCases.deleteHomeworkTaskUseCase(homeworkTask) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.DELETE_TASK to null,
                    errorVisible = true
                )
            }
            setHomeworkLoading(homework, false)
        }
    }

    fun onHomeworkTaskEditRequest(homeworkTask: HomeworkTask?) {
        state.value = state.value.copy(editHomeworkTask = homeworkTask)
    }

    fun onHomeworkTaskEditRequestConfirm(newContent: String?) {
        if (newContent.isNullOrBlank()) {
            onHomeworkTaskEditRequest(null)
            return
        }
        val homework = state.value.homework.find { it.tasks.any { task -> task.id == state.value.editHomeworkTask?.id } } ?: return
        state.value.editHomeworkTask?.let {
            if (newContent == it.content) return@let
            if (newContent.isBlank()) return@let
            viewModelScope.launch {
                setHomeworkLoading(homework, true)
                onHomeworkTaskEditRequest(null)
                if (homeworkUseCases.editTaskUseCase(it, newContent) == HomeworkModificationResult.FAILED) {
                    state.value = state.value.copy(
                        errorResponse = ErrorOnUpdate.EDIT_TASK to newContent,
                        errorVisible = true
                    )
                }
                setHomeworkLoading(homework, false)
            }
        }
    }

    fun onResetError() {
        state.value = state.value.copy(errorVisible = false)
    }

    fun refresh() {
        if (state.value.isUpdating) return
        viewModelScope.launch {
            homeworkUseCases.updateUseCase()
        }
    }

    fun onHomeworkHideToggle(homework: Homework) {
        viewModelScope.launch {
            setHomeworkLoading(homework, true)
            homeworkUseCases.hideHomeworkUseCase(homework)
            setHomeworkLoading(homework, false)
        }
    }

    fun onToggleShowHidden() {
        state.value = state.value.copy(showHidden = !state.value.showHidden)
    }

    fun onToggleShowDisabled() {
        state.value = state.value.copy(showDisabled = !state.value.showDisabled)
    }

    fun onToggleShowDone() {
        state.value = state.value.copy(showDone = !state.value.showDone)
    }

    fun onHideNotificationBanner() {
        viewModelScope.launch {
            homeworkUseCases.hideHomeworkNotificationBannerUseCase()
        }
    }

    fun onUpdateDueDate(homework: Homework, newDate: LocalDate) {
        viewModelScope.launch {
            setHomeworkLoading(homework, true)
            if (homeworkUseCases.updateDueDateUseCase(homework, newDate) == HomeworkModificationResult.FAILED) {
                state.value = state.value.copy(
                    errorResponse = ErrorOnUpdate.CHANGE_HOMEWORK_STATE to newDate,
                    errorVisible = true
                )
            }
        }
    }

    fun onEnableHomework() {
        viewModelScope.launch {
            val profile = state.value.profile as? ClassProfile ?: return@launch
            homeworkUseCases.updateHomeworkEnabledUseCase(profile, true)
        }
    }
}

data class HomeworkState(
    val homework: List<Homework> = emptyList(),
    val wrongProfile: Boolean = false,
    val profile: Profile? = null,
    val homeworkDeletionRequest: Homework? = null,
    val homeworkChangeVisibilityRequest: Homework? = null,
    val homeworkTaskDeletionRequest: HomeworkTask? = null,
    val editHomeworkTask: HomeworkTask? = null,
    val errorResponse: Pair<ErrorOnUpdate, Any?>? = null,
    val errorVisible: Boolean = false,
    val isUpdating: Boolean = false,
    val showHidden: Boolean = false,
    val showDisabled: Boolean = false,
    val showDone: Boolean = false,
    val showNotificationBanner: Boolean = false,

    val loadingHomeworkIds: List<Int> = emptyList(),
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
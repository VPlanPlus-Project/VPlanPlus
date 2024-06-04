package es.jvbabi.vplanplus.feature.main_homework.add.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.AddHomeworkUseCases
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddHomeworkViewModel @Inject constructor(
    private val addHomeworkUseCases: AddHomeworkUseCases,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
) : ViewModel() {

    val state = mutableStateOf(AddHomeworkState())

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    getCurrentIdentityUseCase(),
                    addHomeworkUseCases.isShowNewLayoutBalloonUseCase()
                )
            ) { data ->
                val identity = data[0] as Identity?
                val showNewLayoutBalloon = data[1] as Boolean

                if (identity?.school == null) return@combine null
                if (identity.profile == null) return@combine null

                val defaultLessons = addHomeworkUseCases.getDefaultLessonsUseCase()
                val defaultLessonsFiltered = defaultLessons.any { identity.profile.isDefaultLessonEnabled(it.vpId) }

                state.value.copy(
                    defaultLessons = defaultLessons.filter { identity.profile.isDefaultLessonEnabled(it.vpId) },
                    username = identity.profile.vppId?.name,
                    canUseCloud = identity.profile.vppId != null,
                    saveType = state.value.saveType ?: if (identity.profile.vppId != null) SaveType.CLOUD else SaveType.LOCAL,
                    canShowCloudInfoBanner = addHomeworkUseCases.canShowVppIdBannerUseCase(),
                    defaultLessonsFiltered = defaultLessonsFiltered,
                    initDone = true,
                    showNewSaveButtonLocationBalloon = showNewLayoutBalloon
                )
            }.collect {
                if (it != null) state.value = it
            }
        }
    }

    private fun hideCloudInfoBanner() {
        viewModelScope.launch {
            addHomeworkUseCases.hideVppIdBannerUseCase()
            state.value = state.value.copy(canShowCloudInfoBanner = false)
        }
    }

    fun setLessonDialogOpen(isOpen: Boolean) {
        state.value = state.value.copy(isLessonDialogOpen = isOpen)
    }

    fun setUntilDialogOpen(isOpen: Boolean) {
        state.value = state.value.copy(isUntilDialogOpen = isOpen)
    }

    fun setDefaultLesson(defaultLesson: DefaultLesson?) {
        state.value = state.value.copy(selectedDefaultLesson = defaultLesson)
        setLessonDialogOpen(false)
    }

    fun setUntil(until: LocalDate?) {
        state.value = state.value.copy(until = until)
        setUntilDialogOpen(false)
    }

    /**
     * Request to save the homework, will return if not all requirements are met
     */
    fun requestSave() {
        viewModelScope.launch {
            if (!state.value.canSubmit) return@launch
            state.value = state.value.copy(isLoading = true)
            state.value = state.value.copy(
                result = addHomeworkUseCases.saveHomeworkUseCase(
                    until = state.value.until!!,
                    defaultLesson = state.value.selectedDefaultLesson,
                    tasks = state.value.tasks,
                    shareWithClass = state.value.saveType == SaveType.SHARED,
                    storeInCloud = state.value.saveType != SaveType.LOCAL
                ),
                isLoading = false
            )
        }
    }

    fun onUiAction(event: AddHomeworkUiEvent) {
        when (event) {
            is NewLayoutBalloonDismissed -> dismissNewLayoutBalloon()
            is HideNoVppIdBanner -> hideCloudInfoBanner()
            is DeleteTask -> removeTask(event.index)
            is CreateTask -> addTask(event.content)
            is UpdateTask -> updateTask(event.index, event.content)
            is UpdateSaveType -> setSaveType(event.saveType)
        }
    }

    private fun dismissNewLayoutBalloon() {
        viewModelScope.launch {
            addHomeworkUseCases.hideShowNewLayoutBalloonUseCase()
            state.value = state.value.copy(showNewSaveButtonLocationBalloon = false)
        }
    }

    private fun removeTask(index: Int) {
        state.value = state.value.copy(tasks = state.value.tasks.toMutableList().apply { removeAt(index) })
    }

    private fun addTask(content: String) {
        state.value = state.value.copy(tasks = state.value.tasks + content)
    }

    private fun updateTask(index: Int, content: String) {
        state.value = state.value.copy(tasks = state.value.tasks.toMutableList().apply { set(index, content) })
    }

    private fun setSaveType(saveType: SaveType) {
        state.value = state.value.copy(saveType = saveType)
    }
}

data class AddHomeworkState(
    val initDone: Boolean = false,

    val canUseCloud: Boolean = true,
    val canShowCloudInfoBanner: Boolean = false,
    val username: String? = null,

    val defaultLessonsFiltered: Boolean = false,
    val defaultLessons: List<DefaultLesson> = emptyList(),
    val selectedDefaultLesson: DefaultLesson? = null,
    val isLessonDialogOpen: Boolean = false,

    val isUntilDialogOpen: Boolean = false,
    val until: LocalDate? = null,

    val saveType: SaveType? = null,

    val tasks: List<String> = emptyList(),
    val newTask: String = "",

    val result: HomeworkModificationResult? = null,
    val isLoading: Boolean = false,

    val showNewSaveButtonLocationBalloon: Boolean = false
) {
    val canSubmit: Boolean
        get() = until != null && tasks.isNotEmpty() && !isLoading
}

enum class SaveType {
    LOCAL, CLOUD, SHARED
}

sealed class AddHomeworkUiEvent

data object NewLayoutBalloonDismissed : AddHomeworkUiEvent()
data object HideNoVppIdBanner : AddHomeworkUiEvent()
data class DeleteTask(val index: Int): AddHomeworkUiEvent()
data class CreateTask(val content: String): AddHomeworkUiEvent()
data class UpdateTask(val index: Int, val content: String) : AddHomeworkUiEvent()
data class UpdateSaveType(val saveType: SaveType) : AddHomeworkUiEvent()
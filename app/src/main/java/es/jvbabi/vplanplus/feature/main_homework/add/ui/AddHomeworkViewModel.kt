package es.jvbabi.vplanplus.feature.main_homework.add.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.AddHomeworkUseCases
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
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
            getCurrentIdentityUseCase().collect { identity ->
                if (identity?.school == null) return@collect
                if (identity.profile == null) return@collect

                val defaultLessons = addHomeworkUseCases.getDefaultLessonsUseCase()
                val defaultLessonsFiltered = defaultLessons.any { identity.profile.isDefaultLessonEnabled(it.vpId) }

                state.value = state.value.copy(
                    defaultLessons = defaultLessons.filter { identity.profile.isDefaultLessonEnabled(it.vpId) },
                    username = identity.vppId?.name,
                    canUseCloud = identity.vppId != null,
                    isForAll = identity.vppId != null,
                    canShowCloudInfoBanner = addHomeworkUseCases.canShowVppIdBannerUseCase(),
                    defaultLessonsFiltered = defaultLessonsFiltered,
                    initDone = true
                )
            }
        }
    }

    fun hideCloudInfoBanner() {
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

    fun toggleForAll() {
        state.value = state.value.copy(isForAll = !state.value.isForAll)
    }

    fun addTask() {
        if (state.value.newTask.isBlank() || state.value.tasks.contains(state.value.newTask)) return
        state.value = state.value.copy(tasks = state.value.tasks + state.value.newTask)
        setNewTask("")
    }

    fun modifyTask(before: String, after: String) {
        val tasks = state.value.tasks.toMutableList()
        tasks.remove(before)
        if (after.isNotBlank()) tasks.add(after)
        state.value = state.value.copy(tasks = tasks)
    }

    fun setNewTask(content: String) {
        state.value = state.value.copy(newTask = content)
    }

    fun save() {
        viewModelScope.launch {
            if (!state.value.canSubmit) return@launch
            state.value = state.value.copy(isLoading = true)
            state.value = state.value.copy(
                result = addHomeworkUseCases.saveHomeworkUseCase(
                    until = state.value.until!!,
                    defaultLesson = state.value.selectedDefaultLesson!!,
                    tasks = state.value.tasks,
                    shareWithClass = state.value.isForAll,
                    storeInCloud = state.value.storeInCloud
                ),
                isLoading = false
            )
        }
    }

    fun onToggleCloud() {
        state.value = state.value.copy(
            storeInCloud = !state.value.storeInCloud,
            isForAll = !state.value.storeInCloud
        )
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

    val isForAll: Boolean = true,
    val storeInCloud: Boolean = true,

    val tasks: List<String> = emptyList(),
    val newTask: String = "",

    val result: HomeworkModificationResult? = null,
    val isLoading: Boolean = false,
) {
    val canSubmit: Boolean
        get() = selectedDefaultLesson != null && until != null && tasks.isNotEmpty()
}
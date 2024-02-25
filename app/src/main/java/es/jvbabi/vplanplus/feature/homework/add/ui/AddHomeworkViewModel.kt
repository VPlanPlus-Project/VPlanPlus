package es.jvbabi.vplanplus.feature.homework.add.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.homework.add.domain.AddHomeworkUseCases
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddHomeworkViewModel @Inject constructor(
    private val addHomeworkUseCases: AddHomeworkUseCases,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
): ViewModel() {

    val state = mutableStateOf(AddHomeworkState())

    init {
        viewModelScope.launch {
            getCurrentIdentityUseCase().collect { identity ->
                if (identity?.school == null) return@collect
                state.value = state.value.copy(
                    defaultLessons = addHomeworkUseCases.getDefaultLessonsUseCase(),
                    daysPerWeek = identity.school.daysPerWeek,
                    username = identity.vppId?.name
                )
            }
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
}

data class AddHomeworkState(
    val username: String? = null,
    val daysPerWeek: Int = 5,

    val defaultLessons: List<DefaultLesson> = emptyList(),
    val selectedDefaultLesson: DefaultLesson? = null,
    val isLessonDialogOpen: Boolean = false,

    val isUntilDialogOpen: Boolean = false,
    val until: LocalDate? = null,

    val isForAll: Boolean = true,

    val tasks: List<String> = emptyList(),
    val newTask: String = ""
)
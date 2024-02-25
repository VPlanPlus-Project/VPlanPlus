package es.jvbabi.vplanplus.feature.homework.add.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.homework.add.domain.AddHomeworkUseCases
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddHomeworkViewModel @Inject constructor(
    private val addHomeworkUseCases: AddHomeworkUseCases
): ViewModel() {

    val state = mutableStateOf(AddHomeworkState())

    init {
        viewModelScope.launch {
            state.value = state.value.copy(
                defaultLessons = addHomeworkUseCases.getDefaultLessonsUseCase(),
                daysPerWeek = addHomeworkUseCases.getDaysPerWeekUseCase()
            )
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
}

data class AddHomeworkState(
    val daysPerWeek: Int = 5,

    val defaultLessons: List<DefaultLesson> = emptyList(),
    val selectedDefaultLesson: DefaultLesson? = null,
    val isLessonDialogOpen: Boolean = false,

    val isUntilDialogOpen: Boolean = false,
    val until: LocalDate? = null,
)
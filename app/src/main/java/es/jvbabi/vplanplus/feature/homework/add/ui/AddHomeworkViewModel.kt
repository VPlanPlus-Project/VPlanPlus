package es.jvbabi.vplanplus.feature.homework.add.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.homework.add.domain.AddHomeworkUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHomeworkViewModel @Inject constructor(
    private val addHomeworkUseCases: AddHomeworkUseCases
): ViewModel() {

    val state = mutableStateOf(AddHomeworkState())

    init {
        viewModelScope.launch {
            state.value = state.value.copy(
                defaultLessons = addHomeworkUseCases.getDefaultLessonsUseCase()
            )
        }
    }

    fun setLessonDialogOpen(isOpen: Boolean) {
        state.value = state.value.copy(isLessonDialogOpen = isOpen)
    }

    fun setDefaultLesson(defaultLesson: DefaultLesson?) {
        state.value = state.value.copy(selectedDefaultLesson = defaultLesson)
        setLessonDialogOpen(false)
    }
}

data class AddHomeworkState(
    val defaultLessons: List<DefaultLesson> = emptyList(),
    val selectedDefaultLesson: DefaultLesson? = null,
    val isLessonDialogOpen: Boolean = false
)
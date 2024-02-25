package es.jvbabi.vplanplus.feature.homework.view.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.HomeworkUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val homeworkUseCases: HomeworkUseCases
): ViewModel() {

    val state = mutableStateOf(HomeworkState())

    init {
        viewModelScope.launch {
            homeworkUseCases.getHomeworkUseCase().collect {
                state.value = state.value.copy(
                    homework = it.homework,
                    wrongProfile = it.wrongProfile
                )
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
}

data class HomeworkState(
    val homework: List<Homework> = emptyList(),
    val wrongProfile: Boolean = false
)
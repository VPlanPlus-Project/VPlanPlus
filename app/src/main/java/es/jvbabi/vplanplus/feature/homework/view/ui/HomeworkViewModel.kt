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
                    homework = homework.homework,
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
}

data class HomeworkState(
    val homework: List<Homework> = emptyList(),
    val wrongProfile: Boolean = false,
    val identity: Identity = Identity()
)
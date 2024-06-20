package es.jvbabi.vplanplus.feature.main_homework.view.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.HomeworkDetailUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeworkDetailViewModel @Inject constructor(
    private val homeworkDetailUseCases: HomeworkDetailUseCases
): ViewModel() {

    var state by mutableStateOf(HomeworkDetailState())

    fun init(homeworkId: Int) {
        viewModelScope.launch {
            combine(
                listOf(
                    homeworkDetailUseCases.getCurrentIdentityUseCase(),
                    homeworkDetailUseCases.getHomeworkByIdUseCase(homeworkId)
                )
            ) { data ->
                val identity = data[0] as Identity
                val homework = data[1] as Homework
                state.copy(
                    homework = homework,
                    currentIdentity = identity
                )
            }.collect {
                state = it
            }
        }
    }

    fun onAction(action: UiAction) {
        viewModelScope.launch {
            when (action) {
                is TaskDoneStateToggledAction -> homeworkDetailUseCases.taskDoneUseCase(action.homeworkTask, !action.homeworkTask.done)
            }
        }
    }
}

data class HomeworkDetailState(
    val homework: Homework? = null,
    val currentIdentity: Identity? = null
)

sealed class UiAction

data class TaskDoneStateToggledAction(val homeworkTask: HomeworkTask) : UiAction()
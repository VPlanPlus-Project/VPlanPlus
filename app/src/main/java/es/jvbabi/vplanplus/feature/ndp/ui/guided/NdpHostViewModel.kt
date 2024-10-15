package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetNextSchoolDayUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided.NdpGuidedUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NdpHostViewModel @Inject constructor(
    private val getNextSchoolDayUseCase: GetNextSchoolDayUseCase,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val ndpGuidedUseCases: NdpGuidedUseCases
) : ViewModel() {
    var state by mutableStateOf(NdpHostState())
        private set

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    getNextSchoolDayUseCase(fast = false),
                    getCurrentProfileUseCase()
                )
            ) { data ->
                val schoolDay = data[0] as SchoolDay?
                val profile = data[1] as ClassProfile?
                state.copy(
                    nextSchoolDay = schoolDay,
                    currentProfile = profile
                )
            }.collect { state = it }
        }
    }

    fun doAction(action: NdpEvent) {
        when (action) {
            is NdpEvent.Start -> state = state.copy(displayStage = NdpStage.HOMEWORK, currentStage = NdpStage.HOMEWORK)
            is NdpEvent.ChangePage -> state = state.copy(displayStage = action.stage)
            is NdpEvent.ToggleTask -> {
                viewModelScope.launch {
                    ndpGuidedUseCases.toggleTaskDoneStateUseCase(state.currentProfile!!, action.task, !action.task.isDone)
                }
            }
            is NdpEvent.HideHomework -> {
                viewModelScope.launch {
                    if (action.homework is PersonalizedHomework.CloudHomework) {
                        ndpGuidedUseCases.toggleHomeworkHiddenUseCase(action.homework)
                    }
                }
            }
        }
    }
}

enum class NdpStage {
    START, HOMEWORK
}

data class NdpHostState(
    val currentProfile: ClassProfile? = null,
    val nextSchoolDay: SchoolDay? = null,
    val displayStage: NdpStage = NdpStage.START,
    val currentStage: NdpStage = NdpStage.START
)

sealed class NdpEvent {
    data object Start: NdpEvent()
    data class ChangePage(val stage: NdpStage): NdpEvent()

    data class ToggleTask(val task: HomeworkTaskDone): NdpEvent()
    data class HideHomework(val homework: PersonalizedHomework): NdpEvent()
}
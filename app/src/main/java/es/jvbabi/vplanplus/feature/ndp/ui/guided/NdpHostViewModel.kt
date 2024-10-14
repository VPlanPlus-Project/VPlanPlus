package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetNextSchoolDayUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NdpHostViewModel @Inject constructor(
    private val getNextSchoolDayUseCase: GetNextSchoolDayUseCase
) : ViewModel() {
    var state by mutableStateOf(NdpHostState())
        private set

    init {
        viewModelScope.launch {
            getNextSchoolDayUseCase().collect { schoolDay ->
                state = state.copy(nextSchoolDay = schoolDay)
            }
        }
    }

    fun doAction(action: NdpEvent) {
        when (action) {
            is NdpEvent.Start -> state = state.copy(displayStage = NdpStage.HOMEWORK, currentStage = NdpStage.HOMEWORK)
            is NdpEvent.ChangePage -> state = state.copy(displayStage = action.stage)
        }
    }
}

enum class NdpStage {
    START, HOMEWORK
}

data class NdpHostState(
    val nextSchoolDay: SchoolDay? = null,
    val displayStage: NdpStage = NdpStage.START,
    val currentStage: NdpStage = NdpStage.START
)

sealed class NdpEvent {
    data object Start: NdpEvent()
    data class ChangePage(val stage: NdpStage): NdpEvent()
}
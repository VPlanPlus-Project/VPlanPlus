package es.jvbabi.vplanplus.ui.common.dialog.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.ui.common.dialog.domain.usecase.SelectDateUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SelectDateViewModel @Inject constructor(
    private val selectDateUseCases: SelectDateUseCases
) : ViewModel() {

    var state by mutableStateOf(SelectDateUiState())
        private set

    private val uiSyncJobs = mutableMapOf<LocalDate, Job>()

    init {
        viewModelScope.launch {
            selectDateUseCases.getCurrentProfileUseCase().collect { currentProfile ->
                if (currentProfile == null) return@collect
                state = state.copy(currentProfile = currentProfile)
                startOneMonthAroundDate(LocalDate.now())
            }
        }
    }

    private fun startOneMonthAroundDate(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            startDayUiSync(date)
            repeat(31) {
                startDayUiSync(date.plusDays(it.toLong()-31))
                startDayUiSync(date.plusDays(it.toLong()))
            }
        }
    }

    private fun startDayUiSync(date: LocalDate) {
        if (uiSyncJobs[date]?.isActive == true) return
        uiSyncJobs[date] = viewModelScope.launch {
            selectDateUseCases.getDayUseCase(date).collect { day ->
                state = state.copy(
                    days = state.days + (date to day),
                )
            }
        }
    }

    fun doAction(event: SelectDateDialogEvent) {
        when (event) {
            is SelectDateDialogEvent.SelectDate -> state = state.copy(selectedDate = event.date)
        }
    }
}

data class SelectDateUiState(
    val currentProfile: Profile? = null,
    val selectedDate: LocalDate? = null,
    val days: Map<LocalDate, SchoolDay> = emptyMap(),
)

sealed class SelectDateDialogEvent {
    data class SelectDate(val date: LocalDate): SelectDateDialogEvent()
}
package es.jvbabi.vplanplus.feature.home_screen_v2.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.HomeUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases
): ViewModel() {
    var state by mutableStateOf(HomeState())

    private var uiUpdateJobs: Map<LocalDate, Job> = emptyMap()

    init {
        viewModelScope.launch {
            homeUseCases.getCurrentTimeUseCase().collect { state = state.copy(currentTime = it) }
        }
        viewModelScope.launch {
            combine(
                listOf(
                    homeUseCases.getCurrentIdentityUseCase()
                )
            ) { data ->
                val currentIdentity = data[0] as Identity

                state.copy(
                    currentIdentity = currentIdentity,
                )
            }.collect {
                state = it
                restartUiUpdateJobs()
            }
        }
    }

    fun setSearchState(to: Boolean) { state = state.copy(isSearchExpanded = to) }
    fun setSelectedDate(date: LocalDate) {
        state = state.copy(selectedDate = date)
        triggerLessonUiSync(date)
        triggerLessonUiSync(date.minusDays(1L))
        triggerLessonUiSync(date.plusDays(1L))
    }

    private fun restartUiUpdateJobs() {
        uiUpdateJobs.values.forEach { it.cancel() }
        uiUpdateJobs = emptyMap()
        setSelectedDate(state.selectedDate)
    }

    private fun triggerLessonUiSync(date: LocalDate) {
        if (state.days.containsKey(date) || state.currentIdentity?.profile == null) return
        viewModelScope.launch {
            homeUseCases.getDayUseCase(date, state.currentIdentity!!.profile!!).collect {
                state = state.copy(days = state.days + (date to it))
            }
        }
    }
}

data class HomeState(
    val currentTime: ZonedDateTime = ZonedDateTime.now(),
    val currentIdentity: Identity? = null,
    val days: Map<LocalDate, Day> = emptyMap(),
    val selectedDate: LocalDate = LocalDate.now(),

    // search
    val isSearchExpanded: Boolean = false
)
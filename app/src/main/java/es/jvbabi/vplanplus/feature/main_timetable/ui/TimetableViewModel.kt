package es.jvbabi.vplanplus.feature.main_timetable.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.timetable.TimetableUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val timetableUseCases: TimetableUseCases
) : ViewModel() {
    private val _state = mutableStateOf(TimetableState())
    val state: State<TimetableState> = _state

    private var job: Job? = null
    private val days = mutableSetOf<LocalDate>()

    init {
        init(LocalDate.now(), true)
    }

    fun init(date: LocalDate, directNeighbors: Boolean = false) {
        days.add(date)
        if (directNeighbors) {
            days.add(date.minusDays(1))
            days.add(date.plusDays(1))
        }
        job?.cancel()
        job = viewModelScope.launch {
            timetableUseCases.getDataUseCase(days).collect {
                _state.value = _state.value.copy(
                    isLoading = false,
                    activeProfile = it.profile,
                    version = it.version,
                    days = it.days
                )
            }
        }
    }

    fun setDate(date: LocalDate) {
        _state.value = _state.value.copy(date = date)
    }
}

data class TimetableState(
    val isLoading: Boolean = true,
    val activeProfile: Profile? = null,
    val version: Long = 0,
    val days: Map<LocalDate, Day?> = emptyMap(),
    val date: LocalDate = LocalDate.now()
)
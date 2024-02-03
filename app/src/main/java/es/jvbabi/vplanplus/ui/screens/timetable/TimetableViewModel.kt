package es.jvbabi.vplanplus.ui.screens.timetable

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository,
    private val getActiveProfileUseCase: GetCurrentProfileUseCase
) : ViewModel() {
    private val _state = mutableStateOf(TimetableState())
    val state: State<TimetableState> = _state

    private val jobs: HashMap<LocalDate, Job> = hashMapOf()

    init {
        viewModelScope.launch {
            combine(
                getActiveProfileUseCase(),
                keyValueRepository.getFlow(Keys.LESSON_VERSION_NUMBER)
            ) { profile, version ->
                _state.value.copy(
                    activeProfile = profile,
                    version = version?.toLongOrNull() ?: 0
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun init(date: LocalDate, directNeighbors: Boolean = false) {
        viewModelScope.launch {
            while (_state.value.activeProfile == null) delay(50)
            if (jobs.containsKey(date)) return@launch
            jobs[date] = viewModelScope.launch {
                planRepository.getDayForProfile(_state.value.activeProfile!!, date, _state.value.version).collect { day ->
                    _state.value = _state.value.copy(
                        days = _state.value.days + (date to day),
                        isLoading = false
                    )
                }
            }
            if (directNeighbors) {
                init(date.minusDays(1))
                init(date.plusDays(1))
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
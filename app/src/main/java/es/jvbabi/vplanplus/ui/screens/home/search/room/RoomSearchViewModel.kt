package es.jvbabi.vplanplus.ui.screens.home.search.room

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.find_room.FindRoomUseCases
import es.jvbabi.vplanplus.domain.usecase.find_room.RoomMap
import es.jvbabi.vplanplus.domain.usecase.find_room.RoomRecord
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentSchoolUseCase
import es.jvbabi.vplanplus.util.DateUtils
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RoomSearchViewModel @Inject constructor(
    private val findRoomUseCases: FindRoomUseCases,
    private val findCurrentSchoolUseCase: GetCurrentSchoolUseCase
) : ViewModel() {

    private val _state = mutableStateOf(RoomSearchState())
    val state: State<RoomSearchState> = _state

    init {
        viewModelScope.launch {
            findCurrentSchoolUseCase().collect { school ->
                if (school != null) {
                    val roomMap = findRoomUseCases.getRoomMapUseCase(school)
                    _state.value = _state.value.copy(
                        currentSchool = school,
                        rooms = roomMap,
                        loading = false
                    )
                    filter()
                }
            }
        }
    }

    fun filter() {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val now = LocalDateTime.now().format(timeFormatter)
        var filteredRoomMap = state.value.rooms?.rooms?:return
        if (state.value.roomFilter.isNotBlank()) {
            filteredRoomMap = filteredRoomMap.filter { it.room.name.contains(state.value.roomFilter, ignoreCase = true) }
        }
        if (state.value.filterNow) {
            filteredRoomMap = filteredRoomMap.filter { !it.availability.any { a -> a != null && DateUtils.calculateProgress(a.start, now, a.end)!! in 0.0..1.0 } }
        }
        if (state.value.filterNext) {
            filteredRoomMap = filteredRoomMap.filter { room ->
                val currentLesson = room.availability.indexOfFirst { a -> a != null && DateUtils.calculateProgress(a.start, now, a.end)!! in 0.0..1.0 }
                try {
                    room.availability[currentLesson+1] == null
                } catch (e: NullPointerException) {
                    true
                }
            }
        }

        _state.value = _state.value.copy(roomsFiltered = filteredRoomMap)
    }

    fun onRoomFilterValueChanged(newValue: String) {
        _state.value = _state.value.copy(roomFilter = newValue)
        filter()
    }

    fun toggleFilterNow() {
        _state.value = _state.value.copy(filterNow = !_state.value.filterNow)
        filter()
    }

    fun toggleFilterNext() {
        _state.value = _state.value.copy(filterNext = !_state.value.filterNext)
        filter()
    }
}

data class RoomSearchState(
    val currentSchool: School? = null,
    val rooms: RoomMap? = null,
    val roomsFiltered: List<RoomRecord> = emptyList(),
    val loading: Boolean = true,
    val roomFilter: String = "",
    val filterNow: Boolean = false,
    val filterNext: Boolean = true,
)


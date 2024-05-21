package es.jvbabi.vplanplus.feature.room_search.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.RoomSearchUseCases
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.RoomState
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.progress
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class RoomSearchViewModel @Inject constructor(
    private val roomSearchUseCases: RoomSearchUseCases,
    private val getCurrentTimeUseCase: GetCurrentTimeUseCase
) : ViewModel() {

    var state by mutableStateOf(RoomSearchState())
    private var filterJob: Job? = null

    init {
        viewModelScope.launch {
            val identity =  roomSearchUseCases.getCurrentIdentityUseCase().first() ?: return@launch
            state = state.copy(currentIdentity = identity)

            val map = roomSearchUseCases.getRoomMapUseCase(identity)
            val lessonTimes = roomSearchUseCases.getLessonTimesUseCases(identity.profile!!)
            state = state.copy(
                data = map,
                lessonTimes = lessonTimes
            )
        }

        viewModelScope.launch {
            getCurrentTimeUseCase().collect { time ->
                val currentLesson = state.lessonTimes.values.firstOrNull {
                    time.atBeginningOfTheWorld().progress(it.start, it.end) in 0.0..1.0
                }

                val nextLesson = state.lessonTimes.values.firstOrNull {
                    time.atBeginningOfTheWorld().progress(it.start, it.end) < 0.0
                }
                state = state.copy(
                    currentTime = time,
                    currentLessonTime = currentLesson,
                    nextLessonTime = nextLesson,
                    filterRoomsAvailableNowActive = if (currentLesson != null) state.filterRoomsAvailableNowActive else false,
                    filterRoomsAvailableNextLessonActive = if (nextLesson != null) state.filterRoomsAvailableNextLessonActive else false
                )
            }
        }
    }

    fun onTapOnMatrix(time: ZonedDateTime?, room: Room?) {
        state = state.copy(selectedTime = time, selectedRoom = room)
    }

    fun onRoomNameQueryChanged(query: String) {
        state = state.copy(roomNameQuery = query)
        updateSearchResults()
    }

    fun onToggleNowFilter() {
        if (state.currentLessonTime != null) {
            state = state.copy(filterRoomsAvailableNowActive = !state.filterRoomsAvailableNowActive)
            updateSearchResults()
        } else {
            state = state.copy(filterRoomsAvailableNowActive = false)
        }
    }

    fun onToggleNextFilter() {
        if (state.nextLessonTime != null) {
            state = state.copy(filterRoomsAvailableNextLessonActive = !state.filterRoomsAvailableNextLessonActive)
            updateSearchResults()
        } else {
            state = state.copy(filterRoomsAvailableNextLessonActive = false)
        }
    }

    private fun updateSearchResults() {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            val query = state.roomNameQuery.lowercase()
            val data  = state.data.map {
                val matchesQuery = query.isBlank() || it.room.name.lowercase().contains(query)
                val satisfiesCurrentLessonFilter =
                    !state.filterRoomsAvailableNowActive || state.currentLessonTime == null ||
                            it.getOccupiedTimes().none { times -> times.overlaps(state.currentLessonTime!!.toTimeSpan(state.currentTime)) }

                val satisfiesNextLessonFilter =
                    !state.filterRoomsAvailableNextLessonActive || state.nextLessonTime == null ||
                            it.getOccupiedTimes().none { times -> times.overlaps(state.nextLessonTime!!.toTimeSpan(state.currentTime)) }
                it.copy(isExpanded = matchesQuery && satisfiesCurrentLessonFilter && satisfiesNextLessonFilter)
            }
            state = state.copy(data = data)
        }
    }
}

data class RoomSearchState(
    val currentIdentity: Identity? = null,
    val currentTime: ZonedDateTime = ZonedDateTime.now(),
    val data: List<RoomState> = emptyList(),
    val lessonTimes: Map<Int, LessonTime> = emptyMap(),
    val selectedTime: ZonedDateTime? = null,
    val selectedRoom: Room? = null,
    val roomNameQuery: String = "",

    val currentLessonTime: LessonTime? = null,
    val filterRoomsAvailableNowActive: Boolean = false,
    val nextLessonTime: LessonTime? = null,
    val filterRoomsAvailableNextLessonActive: Boolean = false
)
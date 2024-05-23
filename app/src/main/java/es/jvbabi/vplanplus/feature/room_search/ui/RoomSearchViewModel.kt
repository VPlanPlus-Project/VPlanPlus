package es.jvbabi.vplanplus.feature.room_search.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomAbility
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.RoomSearchUseCases
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.RoomState
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.atDate
import es.jvbabi.vplanplus.util.DateUtils.progress
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class RoomSearchViewModel @Inject constructor(
    private val roomSearchUseCases: RoomSearchUseCases,
    private val getCurrentTimeUseCase: GetCurrentTimeUseCase,
    private val getClassByProfileUseCase: GetClassByProfileUseCase
) : ViewModel() {

    var state by mutableStateOf(RoomSearchState())
    private var filterJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    roomSearchUseCases.getCurrentIdentityUseCase(),
                    roomSearchUseCases.canBookRoomUseCase()
                )
            ) { data ->
                val identity = data[0] as Identity? ?: return@combine null
                val canBookRoom = data[1] as BookRoomAbility

                val map = roomSearchUseCases.getRoomMapUseCase(identity)
                val lessonTimes = roomSearchUseCases.getLessonTimesUseCases(identity.profile!!)

                state.copy(
                    currentIdentity = identity,
                    canBookRoom = canBookRoom,
                    data = map,
                    lessonTimes = lessonTimes,
                    currentClass = if (identity.profile.type == ProfileType.STUDENT) getClassByProfileUseCase(identity.profile) else null
                )
            }.collect {
                state = it ?: return@collect
            }
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
        val selectedLessonTime = if (time == null) null else state.lessonTimes.values.firstOrNull { time.atBeginningOfTheWorld().progress(it.start, it.end) in 0.0..1.0 }
        val tappedOnSameSpot = state.selectedRoom == room && room != null && state.selectedLessonTime == selectedLessonTime && selectedLessonTime != null
        val roomHasEventsAtSelectedTimeSpan = if (selectedLessonTime == null) false else state.data
            .any { it.room == room && it.getOccupiedTimes().any { times -> times.overlaps(selectedLessonTime.toTimeSpan(state.currentTime)) } }

        if (tappedOnSameSpot && !roomHasEventsAtSelectedTimeSpan) onRequestBookingForSelectedContext()

        state = state.copy(
            selectedTime = time,
            selectedRoom = room,
            selectedLessonTime = selectedLessonTime
        )
    }

    fun onRequestBookingForSelectedContext() {
        state = state.copy(
            newRoomBookingRequest = NewRoomBookingRequest(
                room = state.selectedRoom ?: return,
                start = state.selectedLessonTime?.start ?: return,
                end = state.selectedLessonTime?.end ?: return
            )
        )
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

                val satisfiesMyBookingsFilter = !state.filterMyBookingsEnabled || it.bookings.any { booking -> (booking.bookedBy?.id ?: -1) == state.currentIdentity?.profile?.vppId?.id }
                it.copy(isExpanded = matchesQuery && satisfiesCurrentLessonFilter && satisfiesNextLessonFilter && satisfiesMyBookingsFilter)
            }
            state = state.copy(data = data)
        }
    }

    fun onCancelBooking() {
        state = state.copy(newRoomBookingRequest = null)
    }

    fun onConfirmBooking(context: Context) {
        val request = state.newRoomBookingRequest ?: return
        viewModelScope.launch {
            state = state.copy(
                bookingResult = roomSearchUseCases.bookRoomUseCase(request.room, request.start.atDate(state.currentTime), request.end.atDate(state.currentTime)),
                newRoomBookingRequest = null
            )
            when (state.bookingResult) {
                BookResult.CONFLICT -> context.getString(R.string.searchAvailableRoom_bookConflict)
                BookResult.NO_INTERNET -> context.getString(R.string.searchAvailableRoom_bookNoInternet)
                BookResult.OTHER -> context.getString(R.string.searchAvailableRoom_bookOther)
                BookResult.SUCCESS -> context.getString(R.string.searchAvailableRoom_bookSuccess)
                null -> null
            }?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onToggleMyBookingsFilter() {
        state = state.copy(filterMyBookingsEnabled = !state.filterMyBookingsEnabled)
        updateSearchResults()
    }
}

data class RoomSearchState(
    val currentIdentity: Identity? = null,
    val currentClass: Classes? = null,
    val currentTime: ZonedDateTime = ZonedDateTime.now(),
    val data: List<RoomState> = emptyList(),
    val lessonTimes: Map<Int, LessonTime> = emptyMap(),
    val selectedTime: ZonedDateTime? = null,
    val selectedRoom: Room? = null,
    val selectedLessonTime: LessonTime? = null,
    val roomNameQuery: String = "",

    val newRoomBookingRequest: NewRoomBookingRequest? = null,
    val canBookRoom: BookRoomAbility = BookRoomAbility.CAN_BOOK,
    val bookingResult: BookResult? = null,

    val cancelBookingRequest: RoomBooking? = null,

    val currentLessonTime: LessonTime? = null,
    val filterRoomsAvailableNowActive: Boolean = false,
    val nextLessonTime: LessonTime? = null,
    val filterRoomsAvailableNextLessonActive: Boolean = false,

    val filterMyBookingsEnabled: Boolean = false
)

data class NewRoomBookingRequest(
    val room: Room,
    val start: ZonedDateTime,
    val end: ZonedDateTime
)
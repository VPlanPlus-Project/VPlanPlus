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
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomAbility
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.CancelBookingResult
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
) : ViewModel() {

    var state by mutableStateOf(RoomSearchState())
    private var filterJob: Job? = null

    private suspend fun reloadMap(profile: Profile = state.currentProfile!!): RoomSearchState {
        val map = roomSearchUseCases.getRoomMapUseCase(profile)
        val lessonTimes = if (profile is ClassProfile) roomSearchUseCases.getLessonTimesUseCases(profile) else emptyMap()
        return state.copy(
            data = map,
            lessonTimes = lessonTimes
        )
    }

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    roomSearchUseCases.getCurrentProfileUseCase(),
                    roomSearchUseCases.canBookRoomUseCase()
                )
            ) { data ->
                val profile = data[0] as Profile? ?: return@combine null
                val canBookRoom = data[1] as BookRoomAbility

                reloadMap(profile).copy(
                    currentProfile = profile,
                    canBookRoom = canBookRoom
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

                val satisfiesMyBookingsFilter = !state.filterMyBookingsEnabled || it.bookings.any { booking -> (booking.bookedBy?.id ?: -1) == (state.currentProfile as? ClassProfile)?.vppId?.id }
                it.copy(isExpanded = matchesQuery && satisfiesCurrentLessonFilter && satisfiesNextLessonFilter && satisfiesMyBookingsFilter)
            }
            state = state.copy(data = data)
        }
    }

    fun onCancelBookingProgress() {
        state = state.copy(newRoomBookingRequest = null)
    }

    fun onConfirmBooking(context: Context) {
        if (state.isBookingRelatedOperationInProgress) return
        val request = state.newRoomBookingRequest ?: return
        state = state.copy(newRoomBookingRequest = null)
        viewModelScope.launch {
            bookingRelatedOperationStart()
            state = state.copy(bookingResult = roomSearchUseCases.bookRoomUseCase(request.room, request.start.atDate(state.currentTime), request.end.atDate(state.currentTime)))
            bookingRelatedOperationEnd()
            when (state.bookingResult) {
                BookResult.CONFLICT -> context.getString(R.string.searchAvailableRoom_bookConflict)
                BookResult.NO_INTERNET -> context.getString(R.string.searchAvailableRoom_bookNoInternet)
                BookResult.OTHER -> context.getString(R.string.searchAvailableRoom_bookOther)
                BookResult.SUCCESS -> context.getString(R.string.searchAvailableRoom_bookSuccess)
                null -> null
            }?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }

            if (state.bookingResult in listOf(BookResult.SUCCESS, BookResult.CONFLICT)) state = reloadMap()
        }
    }

    fun onToggleMyBookingsFilter() {
        state = state.copy(filterMyBookingsEnabled = !state.filterMyBookingsEnabled)
        updateSearchResults()
    }

    fun onRequestBookingCancellation(booking: RoomBooking) {
        state = state.copy(cancelBookingRequest = booking)
    }
    fun onCancelBookingAborted() {
        state = state.copy(cancelBookingRequest = null)
    }

    fun onCancelBookingConfirmed(context: Context) {
        if (state.isBookingRelatedOperationInProgress) return
        val booking = state.cancelBookingRequest ?: return
        state = state.copy(cancelBookingRequest = null)
        viewModelScope.launch {
            bookingRelatedOperationStart()
            val result = roomSearchUseCases.cancelBookingUseCase(booking)
            bookingRelatedOperationEnd()

            Toast.makeText(
                context,
                when (result) {
                    CancelBookingResult.SUCCESS -> R.string.searchAvailableRoom_cancelBookingSuccess
                    CancelBookingResult.NO_INTERNET -> R.string.noInternet
                    else -> R.string.unknownError
                },
                Toast.LENGTH_SHORT
            ).show()

            if (result == CancelBookingResult.SUCCESS) state = reloadMap()
        }
    }

    private fun bookingRelatedOperationStart() {
        state = state.copy(isBookingRelatedOperationInProgress = true)
    }

    private fun bookingRelatedOperationEnd() {
        state = state.copy(isBookingRelatedOperationInProgress = false)
    }
}

data class RoomSearchState(
    val currentProfile: Profile? = null,
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

    val isBookingRelatedOperationInProgress: Boolean = false,

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
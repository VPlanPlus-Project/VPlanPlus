package es.jvbabi.vplanplus.feature.room_search.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomUseCases
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class BookRoomViewModel @Inject constructor(
    private val bookRoomUseCases: BookRoomUseCases
) : ViewModel() {

    val state = mutableStateOf(BookRoomState())

    fun init(roomName: String) {
        viewModelScope.launch {
            state.value = state.value.copy(currentIdentity = bookRoomUseCases.getCurrentIdentityUseCase().first())
            state.value = state.value.copy(room = bookRoomUseCases.getRoomByNameUseCase(roomName, state.value.currentIdentity!!.school!!))
            state.value = state.value.copy(lessons = bookRoomUseCases.getLessonTimesForClassUseCase(state.value.currentIdentity?.profile?.vppId?.classes!!).values.toList().associateWith { BookTimeState.INACTIVE })
            bookRoomUseCases.showRoomBookingDisclaimerBannerUseCase().collect {
                state.value = state.value.copy(showDisclaimerBanner = it)
            }
        }
    }

    fun toggleLessonTime(index: Int) {
        val currentState = state.value.lessons.filterKeys { it.lessonNumber == index }
        val newState = currentState.mapValues {
            when(it.value) {
                BookTimeState.ACTIVE -> BookTimeState.INACTIVE
                BookTimeState.INACTIVE -> BookTimeState.ACTIVE
                else -> it.value
            }
        }
        state.value = state.value.copy(lessons = state.value.lessons + newState)
    }

    fun hideDisclaimerBanner() {
        viewModelScope.launch {
            bookRoomUseCases.hideRoomBookingDisclaimerBannerUseCase()
        }
    }

    fun confirmBooking() {
        viewModelScope.launch {
            val now = ZonedDateTime.now()
            val results = mutableListOf<Boolean>()

            state.value = state.value.copy(isBookingLoading = true)
            state.value.lessons.filterValues { it == BookTimeState.ACTIVE }.forEach { (lessonTime, _) ->
                val result = bookRoomUseCases.bookRoomUseCase(
                    room = state.value.room ?: return@launch,
                    start = lessonTime.start.withYear(now.year).withDayOfYear(now.dayOfYear),
                    end = lessonTime.end.withYear(now.year).withDayOfYear(now.dayOfYear)
                )
                when (result) {
                    BookResult.CONFLICT -> {
                        state.value = state.value.copy(
                            lessons = state.value.lessons.plus(lessonTime to BookTimeState.CONFLICT)
                        )
                        results.add(false)
                    }
                    BookResult.SUCCESS -> results.add(true)
                    else -> results.add(false)
                }
            }
            state.value = state.value.copy(allSuccessful = results.all { it }, isBookingLoading = false)
        }
    }
}

data class BookRoomState(
    val room: Room? = null,
    val currentIdentity: Identity? = null,
    val lessons: Map<LessonTime, BookTimeState> = emptyMap(),
    val showDisclaimerBanner: Boolean = false,
    val isBookingLoading: Boolean = false,
    val allSuccessful: Boolean? = null,
)

enum class BookTimeState {
    ACTIVE, INACTIVE, CONFLICT
}

fun LessonTime.toTimeString(): String {
    return start.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + end.format(DateTimeFormatter.ofPattern("HH:mm"))
}
package es.jvbabi.vplanplus.ui.screens.home.search.room

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.find_room.BookRoomAbility
import es.jvbabi.vplanplus.domain.usecase.find_room.FindRoomUseCases
import es.jvbabi.vplanplus.domain.usecase.find_room.RoomMap
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentSchoolUseCase
import es.jvbabi.vplanplus.domain.usecase.profile.GetLessonTimesForClassUseCase
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.between
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class RoomSearchViewModel @Inject constructor(
    private val findRoomUseCases: FindRoomUseCases,
    private val findCurrentSchoolUseCase: GetCurrentSchoolUseCase,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val getClassByProfileUseCase: GetClassByProfileUseCase,
    private val getLessonTimesForClassUseCase: GetLessonTimesForClassUseCase,
    private val getCurrentLessonNumberUseCase: GetCurrentLessonNumberUseCase
) : ViewModel() {

    private val _state = mutableStateOf(RoomSearchState())
    val state: State<RoomSearchState> = _state

    private var filterJob: Job? = null

    init {
        viewModelScope.launch {
            init()
        }
    }

    suspend fun init() {
        viewModelScope.launch {
            combine(
                findCurrentSchoolUseCase(),
                getCurrentProfileUseCase(),
                findRoomUseCases.canBookRoomUseCase()
            ) { school, profile, canBookRooms ->
                if (school == null || profile == null) {
                    Log.d("RoomSearchViewModel", "school or profile is null")
                    return@combine state.value
                }
                val roomMap = findRoomUseCases.getRoomMapUseCase(school)

                var profileStart: LocalDateTime? = null
                var currentClass: Classes? = null
                var showFilterChips = false
                var showNowFilter = false
                var nowTimespan: Pair<LocalDateTime, LocalDateTime>? = null
                var nextTimespan: Pair<LocalDateTime, LocalDateTime>? = null

                var lessonTimes: Map<Int, LessonTime>? = null

                if (profile.type == ProfileType.STUDENT) {
                    currentClass = getClassByProfileUseCase(profile)
                    lessonTimes = getLessonTimesForClassUseCase(currentClass!!)
                    var start = lessonTimes.entries.first()
                    if (roomMap.rooms.all { it.lessons.first() == null } && start.key == 0) { // if 0th lesson exists and no room is used in 0th lesson
                        start = lessonTimes.entries.first { it.key > 0 }
                        _state.value = _state.value.copy(showLesson0 = false)
                    }
                    val currentLessonNumber = getCurrentLessonNumberUseCase(currentClass)
                    val times = getLessonTimesForClassUseCase(currentClass)

                    val now =
                        if (currentLessonNumber != null) times[floor(currentLessonNumber).toInt()] else null
                    val next =
                        if (currentLessonNumber != null) times[floor(currentLessonNumber).toInt() + 1] else null

                    nowTimespan = if (now != null) Pair(now.start, now.end) else null

                    nextTimespan = if (next != null) Pair(next.start, next.end) else null
                    profileStart = start.value.start
                    showFilterChips = currentLessonNumber != null && currentLessonNumber + 0.5 != roomMap.maxLessons.toDouble()
                    showNowFilter = (currentLessonNumber ?: 0.0) % 1 != 0.5
                }

                _state.value.copy(
                    currentSchool = school,
                    rooms = roomMap,
                    profileStart = profileStart,
                    currentClass = currentClass,
                    lessonTimes = lessonTimes,
                    loading = false,
                    showFilterChips = showFilterChips,
                    filterNowTimespan = nowTimespan,
                    filterNextTimespan = nextTimespan,
                    showNowFilter = showNowFilter,
                    filterNow = if (!showNowFilter) false else _state.value.filterNow,
                    filterNext = if (!showFilterChips) false else _state.value.filterNext,
                    canBookRoom = canBookRooms,
                )
            }.collect {
                _state.value = it
                filter()
            }
        }
    }

    fun filter() {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            var filteredRoomMap =
                state.value.rooms?.rooms?.map { it.copy(displayed = true) } ?: return@launch

            // filter name
            if (state.value.roomFilter.isNotBlank()) {
                filteredRoomMap = filteredRoomMap.map {
                    if (!it.room.name.contains(state.value.roomFilter, ignoreCase = true)) {
                        it.copy(displayed = false)
                    } else it
                }
            }

            // filter availability now
            if (state.value.filterNow && state.value.filterNowTimespan != null) {

                filteredRoomMap = filteredRoomMap.map { rr ->
                    if (rr.lessons
                            .filterNotNull()
                            .any { l ->
                                l.start.atBeginningOfTheWorld().between(
                                    _state.value.filterNowTimespan!!.first,
                                    _state.value.filterNowTimespan!!.second
                                ) ||
                                        l.end.atBeginningOfTheWorld().between(
                                            _state.value.filterNowTimespan!!.first,
                                            _state.value.filterNowTimespan!!.second
                                        )
                            }
                    ) {
                        rr.copy(displayed = false)
                    } else rr
                }
            }

            if (state.value.filterNext && state.value.filterNextTimespan != null) {
                filteredRoomMap = filteredRoomMap.map { rr ->
                    if (rr.lessons
                            .filterNotNull()
                            .any { l ->
                                l.start.atBeginningOfTheWorld().between(
                                    _state.value.filterNextTimespan!!.first,
                                    _state.value.filterNextTimespan!!.second
                                ) ||
                                        l.end.atBeginningOfTheWorld().between(
                                            _state.value.filterNextTimespan!!.first,
                                            _state.value.filterNextTimespan!!.second
                                        )
                            }
                    ) {
                        rr.copy(displayed = false)
                    } else rr
                }
            }

            _state.value =
                _state.value.copy(rooms = _state.value.rooms?.copy(rooms = filteredRoomMap))
        }
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

    fun showLessonDetailDialog(lesson: Lesson) {
        _state.value = _state.value.copy(detailLesson = lesson)
    }

    fun showBookingDetailDialog(booking: es.jvbabi.vplanplus.domain.model.RoomBooking) {
        _state.value = _state.value.copy(detailBooking = booking)
    }

    fun closeDialog() {
        _state.value = _state.value.copy(detailLesson = null, detailBooking = null)
    }

    fun openBookRoomDialog(room: Room, from: LocalDateTime, to: LocalDateTime) {
        _state.value = _state.value.copy(
            currentRoomBooking = RoomBooking(
                room,
                from,
                to
            )
        )
    }

    fun closeBookRoomDialog() {
        _state.value = _state.value.copy(currentRoomBooking = null)
    }

    fun confirmBooking() {
        viewModelScope.launch {
            if (state.value.currentRoomBooking != null) {
                val today = LocalDate.now()
                _state.value = _state.value.copy(roomBookingResult = null)
                val result = findRoomUseCases.bookRoomUseCase(
                    state.value.currentRoomBooking!!.room,
                    state.value.currentRoomBooking!!.start.withDayOfYear(today.dayOfYear).withYear(today.year),
                    state.value.currentRoomBooking!!.end.withDayOfYear(today.dayOfYear).withYear(today.year)
                )
                _state.value = _state.value.copy(
                    currentRoomBooking = null,
                    roomBookingResult = result
                )
                if (result == BookResult.SUCCESS) init()
            }
        }
    }
}

data class RoomSearchState(
    val currentSchool: School? = null,
    val currentClass: Classes? = null, // only if user is student
    val rooms: RoomMap? = null,
    val loading: Boolean = true,
    val roomFilter: String = "",
    val filterNow: Boolean = false,
    val filterNext: Boolean = true,
    val detailLesson: Lesson? = null,
    val detailBooking: es.jvbabi.vplanplus.domain.model.RoomBooking? = null,
    val showFilterChips: Boolean = false,
    val profileStart: LocalDateTime? = null,
    val showLesson0: Boolean = true,
    val filterNowTimespan: Pair<LocalDateTime, LocalDateTime>? = null,
    val filterNextTimespan: Pair<LocalDateTime, LocalDateTime>? = null,
    val showNowFilter: Boolean = true,
    val lessonTimes: Map<Int, LessonTime>? = null,

    val currentRoomBooking: RoomBooking? = null,
    val canBookRoom: BookRoomAbility = BookRoomAbility.CAN_BOOK,
    val roomBookingResult: BookResult? = null
)

data class RoomBooking(
    val room: Room,
    val start: LocalDateTime,
    val end: LocalDateTime
)


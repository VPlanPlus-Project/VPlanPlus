package es.jvbabi.vplanplus.ui.screens.home.search.room

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.find_room.FindRoomUseCases
import es.jvbabi.vplanplus.domain.usecase.find_room.RoomMap
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentSchoolUseCase
import es.jvbabi.vplanplus.domain.usecase.profile.GetLessonTimesForClassUseCase
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.between
import es.jvbabi.vplanplus.util.DateUtils.toLocalDateTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
            combine(
                findCurrentSchoolUseCase(),
                getCurrentProfileUseCase(),
            ) { school, profile ->
                if (school == null || profile == null) return@combine state.value
                val roomMap = findRoomUseCases.getRoomMapUseCase(school)
                val `class`: Classes? = getClassByProfileUseCase(profile)

                var start = getLessonTimesForClassUseCase(`class`!!).entries.first()
                if (roomMap.rooms.all { it.lessons.first() == null } && start.key == 0) { // if 0th lesson exists and no room is used in 0th lesson
                    start = getLessonTimesForClassUseCase(`class`).entries.first { it.key > 0 }
                    _state.value = _state.value.copy(showLesson0 = false)
                }

                val currentLessonNumber = getCurrentLessonNumberUseCase(`class`)
                val times = getLessonTimesForClassUseCase(`class`)
                val now =
                    if (currentLessonNumber != null) times[floor(currentLessonNumber).toInt()] else null
                val next =
                    if (currentLessonNumber != null) times[floor(currentLessonNumber).toInt() + 1] else null

                val nowTimespan = if (now != null) Pair(
                    "${now.start}:00".toLocalDateTime().atBeginningOfTheWorld(),
                    "${now.end}:00".toLocalDateTime().atBeginningOfTheWorld(),
                ) else null

                val nextTimespan = if (next != null) Pair(
                    "${next.start}:00".toLocalDateTime().atBeginningOfTheWorld(),
                    "${next.end}:00".toLocalDateTime().atBeginningOfTheWorld(),
                ) else null

                _state.value.copy(
                    currentSchool = school,
                    rooms = roomMap,
                    profileStart = "${start.value.start}:00".toLocalDateTime(),
                    currentClass = `class`,
                    loading = false,
                    showFilterChips = currentLessonNumber != null && currentLessonNumber + 0.5 != roomMap.maxLessons.toDouble(),
                    filterNowTimespan = nowTimespan,
                    filterNextTimespan = nextTimespan,
                    showNowFilter = (currentLessonNumber ?: 0.0) % 1 != 0.5,
                    filterNow = if (currentLessonNumber == null) false else _state.value.filterNow,
                    filterNext = if (currentLessonNumber == null) false else _state.value.filterNext,
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

    fun showDialog(lesson: Lesson) {
        _state.value = _state.value.copy(detailLesson = lesson)
    }

    fun closeDialog() {
        _state.value = _state.value.copy(detailLesson = null)
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
    val showFilterChips: Boolean = false,
    val profileStart: LocalDateTime? = null,
    val showLesson0: Boolean = true,
    val filterNowTimespan: Pair<LocalDateTime, LocalDateTime>? = null,
    val filterNextTimespan: Pair<LocalDateTime, LocalDateTime>? = null,
    val showNowFilter: Boolean = true,
)


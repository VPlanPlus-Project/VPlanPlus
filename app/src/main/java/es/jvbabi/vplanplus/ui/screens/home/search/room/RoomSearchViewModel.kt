package es.jvbabi.vplanplus.ui.screens.home.search.room

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
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
import es.jvbabi.vplanplus.util.DateUtils.toLocalDateTime
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
    private val getCurrentLessonNumberUseCase: GetCurrentLessonNumberUseCase,
    private val getLessonTimesForClassUseCase: GetLessonTimesForClassUseCase
) : ViewModel() {

    private val _state = mutableStateOf(RoomSearchState())
    val state: State<RoomSearchState> = _state

    init {
        viewModelScope.launch {
            combine(
                findCurrentSchoolUseCase(),
                getCurrentProfileUseCase()
            ) { school, profile ->
                var `class`: Classes? = null
                if (school == null || profile == null) return@combine state.value
                val roomMap = findRoomUseCases.getRoomMapUseCase(school)
                if (profile.type == ProfileType.STUDENT) {
                    `class` = getClassByProfileUseCase(profile)
                    var start = getLessonTimesForClassUseCase(`class`!!).entries.first()
                    if (roomMap.rooms.all { it.lessons.first() == null } && start.key == 0) { // if 0th lesson exists and no room is used in 0th lesson
                        start = getLessonTimesForClassUseCase(`class`).entries.first { it.key > 0 }
                        _state.value = _state.value.copy(showLesson0 = false)
                    }
                    _state.value = _state.value.copy(
                        profileStart = "${start.value.start}:00".toLocalDateTime()
                    )
                }
                _state.value.copy(
                    currentSchool = school,
                    rooms = roomMap,
                    currentClass = `class`,
                    loading = false,
                    showFilterChips = state.value.currentClass != null && (state.value.currentLesson
                        ?: 0.toDouble()) + 0.5 != state.value.rooms?.maxLessons?.toDouble()
                )
            }.collect {
                _state.value = it
                filter()
            }
        }
    }

    fun filter() {
        viewModelScope.launch {
            var filteredRoomMap =
                state.value.rooms?.rooms?.map { it.copy(displayed = true) } ?: return@launch

            if (state.value.roomFilter.isNotBlank()) {
                filteredRoomMap = filteredRoomMap.map {
                    if (!it.room.name.contains(state.value.roomFilter, ignoreCase = true)) {
                        it.copy(displayed = false)
                    } else it
                }
            }
            if (_state.value.currentClass != null) {
                val currentLessonNumber = getCurrentLessonNumberUseCase(state.value.currentClass!!)
                _state.value = _state.value.copy(currentLesson = currentLessonNumber)
                if (currentLessonNumber == null) return@launch
                if (state.value.filterNow && state.value.currentClass != null) {
                    filteredRoomMap = filteredRoomMap.map {
                        if (it.lessons.any { l -> l?.lessonNumber == floor(currentLessonNumber).toInt() + 1 }) {
                            it.copy(displayed = false)
                        } else it
                    }
                }
                if (state.value.filterNext && state.value.currentClass != null) {
                    try {
                        filteredRoomMap = filteredRoomMap.map {
                            if (it.lessons.any { l -> l?.lessonNumber == floor(currentLessonNumber).toInt() + 2 }) {
                                it.copy(displayed = false)
                            } else it
                        }
                    } catch (_: IndexOutOfBoundsException) {
                    }
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
    val currentLesson: Double? = null,
    val detailLesson: Lesson? = null,
    val showFilterChips: Boolean = false,
    val profileStart: LocalDateTime? = null,
    val showLesson0: Boolean = true
)


package es.jvbabi.vplanplus.ui.screens.home.search.room

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.find_room.FindRoomUseCases
import es.jvbabi.vplanplus.domain.usecase.find_room.RoomMap
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentSchoolUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class RoomSearchViewModel @Inject constructor(
    private val findRoomUseCases: FindRoomUseCases,
    private val findCurrentSchoolUseCase: GetCurrentSchoolUseCase,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val getClassByProfileUseCase: GetClassByProfileUseCase,
    private val getCurrentLessonNumberUseCase: GetCurrentLessonNumberUseCase
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
                }
                _state.value.copy(
                    currentSchool = school,
                    rooms = roomMap,
                    currentClass = `class`,
                    loading = false
                )
            }.collect {
                _state.value = it
                filter()
            }
        }
    }

    fun filter() {
        viewModelScope.launch {

            var filteredRoomMap = state.value.rooms?.rooms?.map { it.copy(displayed = true) }?:return@launch

            if (state.value.roomFilter.isNotBlank()) {
                filteredRoomMap = filteredRoomMap.map {
                    if (!it.room.name.contains(state.value.roomFilter, ignoreCase = true)) {
                        it.copy(displayed = false)
                    } else it
                }
            }
            val currentLessonNumber = getCurrentLessonNumberUseCase(state.value.currentClass!!)
           _state.value = _state.value.copy(currentLesson = currentLessonNumber)
            if (currentLessonNumber == null) return@launch
            if (state.value.filterNow && state.value.currentClass != null) {
                filteredRoomMap = filteredRoomMap.map {
                    if (it.availability[ceil(currentLessonNumber).toInt()] != null) {
                        it.copy(displayed = false)
                    } else it
                }
            }
            if (state.value.filterNext && state.value.currentClass != null) {
                try {
                    filteredRoomMap = filteredRoomMap.map {
                        if (it.availability[ceil(currentLessonNumber).toInt()+1] != null) {
                            it.copy(displayed = false)
                        } else it
                    }
                } catch (_: IndexOutOfBoundsException) {}
            }
            _state.value = _state.value.copy(rooms = _state.value.rooms?.copy(rooms = filteredRoomMap))
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
}

data class RoomSearchState(
    val currentSchool: School? = null,
    val currentClass: Classes? = null, // only if user is student
    val rooms: RoomMap? = null,
    val loading: Boolean = true,
    val roomFilter: String = "",
    val filterNow: Boolean = false,
    val filterNext: Boolean = true,
    val currentLesson: Double? = null
)


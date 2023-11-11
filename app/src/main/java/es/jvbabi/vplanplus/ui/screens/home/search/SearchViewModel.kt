package es.jvbabi.vplanplus.ui.screens.home.search

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.ui.screens.home.DayType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val classUseCases: ClassUseCases,
    private val schoolUseCases: SchoolUseCases,
    private val lessonUseCases: LessonUseCases,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
) : ViewModel() {

    private val _state = mutableStateOf(SearchState())
    val state: State<SearchState> = _state

    fun type(value: String) {
        _state.value = _state.value.copy(searchValue = value)
        if (value.isEmpty()) {
            _state.value = _state.value.copy(result = emptyList())
            return
        }

        Log.d("SearchViewModel", "type: $value")

        viewModelScope.launch {
            val schools = schoolUseCases.getSchools()
            val resultGroups = mutableListOf<ResultGroup>()
            schools.forEach { school ->
                val result = mutableListOf<Result>()
                if (_state.value.filter[FilterType.TEACHER]!!) {
                    val teachers = teacherRepository.getTeachersBySchoolId(school.id!!).filter {
                        it.acronym.lowercase().contains(_state.value.searchValue.lowercase())
                    }
                    val firstTeacher = teachers.firstOrNull()
                    if (firstTeacher != null) {
                        val lessons = lessonUseCases.getLessonsForTeacher(teacherRepository.getTeacherById(firstTeacher.id!!)!!, LocalDate.now()).firstOrNull()
                        teachers.forEachIndexed { index, teacher ->
                            if (index == 0 && lessons != null && lessons.dayType == DayType.DATA) {
                                result.add(
                                    Result(
                                        teacher.id!!,
                                        teacher.acronym,
                                        FilterType.TEACHER,
                                        lessons.lessons
                                    )
                                )
                            } else {
                                result.add(Result(teacher.id!!, teacher.acronym, FilterType.TEACHER))
                            }
                        }
                    }
                }
                if (state.value.filter[FilterType.ROOM]!!) {
                    val rooms = roomRepository.getRoomsBySchool(school).filter { it.name.lowercase().contains(_state.value.searchValue.lowercase()) }
                    val firstRoom = rooms.firstOrNull()
                    if (firstRoom != null) {
                        val lessons = lessonUseCases.getLessonsForRoom(roomRepository.getRoomById(firstRoom.id!!), LocalDate.now()).firstOrNull()
                        rooms.forEachIndexed { index, room ->
                            if (index == 0 && lessons != null && lessons.dayType == DayType.DATA) {
                                result.add(
                                    Result(
                                        room.id!!,
                                        room.name,
                                        FilterType.ROOM,
                                        lessons.lessons
                                    )
                                )
                            } else {
                                result.add(Result(room.id!!, room.name, FilterType.ROOM))
                            }
                        }
                    }
                }

                resultGroups.add(ResultGroup(school, result))
            }

            _state.value = _state.value.copy(result = resultGroups)
        }
    }

    fun toggleFilter(filterType: FilterType) {
        _state.value = _state.value.copy(
            filter = _state.value.filter.plus(
                filterType to !(_state.value.filter[filterType]?:true)
            )
        )
        type(_state.value.searchValue)
    }
}

data class SearchState(
    val searchValue: String = "",
    val filter: Map<FilterType, Boolean> = mapOf(
        FilterType.TEACHER to true,
        FilterType.ROOM to true,
        FilterType.CLASS to true,
        FilterType.PROFILE to true
    ),

    val result: List<ResultGroup> = emptyList()
)

data class ResultGroup(
    val school: School,
    val results: List<Result>
)

data class Result(
    val id: Long,
    val name: String,
    val type: FilterType,
    val lessons: List<es.jvbabi.vplanplus.ui.screens.home.Lesson> = emptyList()
)

enum class FilterType {
    TEACHER,
    ROOM,
    CLASS,
    PROFILE
}
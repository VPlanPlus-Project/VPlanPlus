package es.jvbabi.vplanplus.ui.screens.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.HolidayUseCases
import es.jvbabi.vplanplus.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val classUseCases: ClassUseCases,
    private val profileUseCases: ProfileUseCases,
    private val holidayUseCases: HolidayUseCases,
    private val vPlanUseCases: VPlanUseCases,
    private val schoolUseCases: SchoolUseCases,
    private val homeUseCases: HomeUseCases,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var activeProfile: Profile? = null
    private var school: School? = null

    suspend fun init(oneTime: Boolean = false) {
        viewModelScope.launch {
            activeProfile = profileUseCases.getActiveProfile()
            _state.value = _state.value.copy(activeProfile = activeProfile?.toMenuProfile())
            _state.value =
                _state.value.copy(initDone = true)
            if (activeProfile != null) {
                var schoolId: Long? = null
                when (activeProfile!!.type) {
                    ProfileType.STUDENT -> {
                        val profileClass = classUseCases.getClassById(activeProfile!!.referenceId)
                        schoolId = profileClass.schoolId
                        school = schoolUseCases.getSchoolFromId(schoolId)
                    }
                    ProfileType.TEACHER -> {
                        val profileTeacher = teacherRepository.getTeacherById(activeProfile!!.referenceId)
                        schoolId = profileTeacher!!.schoolId
                        school = schoolUseCases.getSchoolFromId(profileTeacher.schoolId)
                    }
                    ProfileType.ROOM -> {
                        val room = roomRepository.getRoomById(activeProfile!!.referenceId)
                        schoolId = room.schoolId
                        school = schoolUseCases.getSchoolFromId(room.schoolId)
                    }
                }

                val holidays = holidayUseCases.getHolidaysBySchoolId(schoolId)

                _state.value =
                    _state.value.copy(
                        nextHoliday = holidays.find { it.date.isAfter(LocalDate.now()) }?.date,
                        lessons = state.value.lessons.apply {
                            this.remove(state.value.date)
                            this[state.value.date] = homeUseCases.getLessons(activeProfile!!, state.value.date)
                        },
                        profiles = profileUseCases.getProfiles().map { MenuProfile(it.id!!, it.name) }
                    )
            }

            delay(5000)
            if (!oneTime) init()
        }
    }

    suspend fun getVPlanData() {
        _state.value = _state.value.copy(isLoading = true)

        repeat(3) { i ->
            val vPlanData = vPlanUseCases.getVPlanData(school!!, state.value.date.plusDays(i-1L)) // TODO optimize
            if (vPlanData.data == null) {
                Log.d("VPlanData $i", "null")
                _state.value = _state.value.copy(isLoading = false)
                return@repeat
            }
            vPlanUseCases.processVplanData(vPlanData.data)
            Log.d("VPlanData", vPlanData.toString())
        }
        init(true)
        _state.value = _state.value.copy(isLoading = false)
    }

    fun onProfileSelected(profileId: Long) {
        viewModelScope.launch {
            profileUseCases.setActiveProfile(profileId)
            init(true)
        }
    }

    fun setViewType(viewType: ViewType) {
        _state.value = _state.value.copy(viewMode = viewType)
    }
}

data class HomeState(
    val initDone: Boolean = false,
    val nextHoliday: LocalDate? = null,
    val lessons: HashMap<LocalDate, List<Lesson>> = hashMapOf(),
    val isLoading: Boolean = false,
    val profiles: List<MenuProfile> = listOf(),
    val activeProfile: MenuProfile? = null,
    val date: LocalDate = LocalDate.now(),
    val viewMode: ViewType = ViewType.DAY
)

enum class ViewType {
    WEEK, DAY
}
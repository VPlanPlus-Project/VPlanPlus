package es.jvbabi.vplanplus.ui.screens.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.HolidayUseCases
import es.jvbabi.vplanplus.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
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
    private val lessonUseCases: LessonUseCases,
    private val homeUseCases: HomeUseCases
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var activeProfile: Profile? = null
    private var school: School? = null

    suspend fun init() {
        activeProfile = profileUseCases.getActiveProfile()
        _state.value = _state.value.copy(activeProfile = activeProfile?.toMenuProfile())
        _state.value =
            _state.value.copy(initDone = true)
        if (activeProfile != null) {
            var schoolId: Long? = null
            if (activeProfile!!.type == 0) {
                val profileClass = classUseCases.getClassById(activeProfile!!.referenceId)
                schoolId = profileClass.schoolId
                school = schoolUseCases.getSchoolFromId(schoolId)
            }

            val holidays = holidayUseCases.getHolidaysBySchoolId(schoolId!!)

            _state.value =
                _state.value.copy(
                    nextHoliday = holidays.find { it.date.isAfter(LocalDate.now()) }?.date,
                    lessons = homeUseCases.getTodayLessons(activeProfile!!),
                    profiles = profileUseCases.getProfiles().map { MenuProfile(it.id!!, it.name) }
                )
        }
    }

    suspend fun getVPlanData() {
        _state.value = _state.value.copy(isLoading = true)
        val vPlanData = vPlanUseCases.getVPlanData(school!!, LocalDate.now())
        if (vPlanData.data == null) {
            Log.d("VPlanData", "null")
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        vPlanUseCases.processVplanData(vPlanData.data)
        Log.d("VPlanData", vPlanData.toString())
        init()
        _state.value = _state.value.copy(isLoading = false)
    }

    fun onProfileSelected(profileId: Long) {
        viewModelScope.launch {
            profileUseCases.setActiveProfile(profileId)

            init()
        }
    }
}

data class HomeState(
    val initDone: Boolean = false,
    val nextHoliday: LocalDate? = null,
    val lessons: List<Lesson> = listOf(),
    val isLoading: Boolean = false,
    val profiles: List<MenuProfile> = listOf(),
    val activeProfile: MenuProfile? = null
)
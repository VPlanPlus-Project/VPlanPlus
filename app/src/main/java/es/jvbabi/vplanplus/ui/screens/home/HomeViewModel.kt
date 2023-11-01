package es.jvbabi.vplanplus.ui.screens.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
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
import es.jvbabi.vplanplus.util.DateUtils
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
        _state.value =
            _state.value.copy(initDone = true, activeProfileFound = activeProfile != null)
        if (activeProfile != null) {
            var schoolId: Long? = null
            if (activeProfile!!.type == 0) {
                val profileClass = classUseCases.getClassById(activeProfile!!.referenceId)
                _state.value = _state.value.copy(activeProfileShortText = profileClass.className)
                schoolId = profileClass.schoolId
                school = schoolUseCases.getSchoolFromId(schoolId)
            }

            val holidays = holidayUseCases.getHolidaysBySchoolId(schoolId!!)

            _state.value =
                _state.value.copy(
                    nextHoliday = holidays.find { it.timestamp > DateUtils.getCurrentDayTimestamp() }
                    ?.let { DateUtils.getDateFromTimestamp(it.timestamp) },
                    lessons = homeUseCases.getTodayLessons(activeProfile!!)
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
}

data class HomeState(
    val initDone: Boolean = false,
    val activeProfileFound: Boolean = false,
    val activeProfileShortText: String = "",
    val nextHoliday: LocalDate? = null,
    val lessons: List<Lesson> = listOf(),
    val isLoading: Boolean = false
)
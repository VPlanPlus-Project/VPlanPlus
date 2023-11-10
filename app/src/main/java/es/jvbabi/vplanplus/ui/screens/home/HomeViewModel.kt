package es.jvbabi.vplanplus.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val classUseCases: ClassUseCases,
    private val profileUseCases: ProfileUseCases,
    private val vPlanUseCases: VPlanUseCases,
    private val schoolUseCases: SchoolUseCases,
    private val homeUseCases: HomeUseCases,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private lateinit var activeProfile: Profile
    private var school: School? = null

    suspend fun init(context: Context) {
        // Check if notification permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            _state.value = _state.value.copy(
                notificationPermissionGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            _state.value = _state.value.copy(notificationPermissionGranted = true)
        }

        // Redirect to onboarding if no profiles are found
        if (profileUseCases.getActiveProfile() == null) {
            _state.value = _state.value.copy(initDone = true)
            Log.d("HomeViewModel", "init; no active profile")
            return
        }
        activeProfile = profileUseCases.getActiveProfile()!!
        Log.d("HomeViewModel", "init; activeProfile=$activeProfile")
        _state.value =
            _state.value.copy(activeProfile = activeProfile.toMenuProfile(), lessons = mapOf())
        val startOfWeek = state.value.date.atStartOfWeek()
        val schoolId: Long?
        when (activeProfile.type) {
            ProfileType.STUDENT -> {
                val profileClass = classUseCases.getClassById(activeProfile.referenceId)
                schoolId = profileClass.schoolId
                school = schoolUseCases.getSchoolFromId(schoolId)
            }

            ProfileType.TEACHER -> {
                val profileTeacher = teacherRepository.getTeacherById(activeProfile.referenceId)!!
                school = schoolUseCases.getSchoolFromId(profileTeacher.schoolId)
            }

            ProfileType.ROOM -> {
                val room = roomRepository.getRoomById(activeProfile.referenceId)
                school = schoolUseCases.getSchoolFromId(room.schoolId)
            }
        }

        repeat(5) { i ->
            Log.d(
                "HomeViewModel",
                "Updating view $i for ${activeProfile.name} at ${startOfWeek.plusDays(i.toLong())}"
            )
            updateView(activeProfile, startOfWeek.plusDays(i.toLong()))
        }

        _state.value =
            _state.value.copy(profiles = profileUseCases.getProfiles().map { it.toMenuProfile() })

        _state.value =
            _state.value.copy(initDone = true)
    }

    @OptIn(FlowPreview::class)
    private fun updateView(profile: Profile, date: LocalDate) {
        if (!_state.value.lessons.containsKey(date)) _state.value =
            _state.value.copy(lessons = state.value.lessons.plus(date to listOf()))
        viewModelScope.launch {
            var first = true
            homeUseCases.getLessons(profile, date).debounce{if (first) 0L else { first = false; 1000L }}.collect { lessons ->
                _state.value =
                    _state.value.copy(
                        lessons = state.value.lessons.plus(date to lessons),
                    )
            }
        }
    }

    suspend fun getVPlanData() {
        _state.value = _state.value.copy(isLoading = true)

        val startOfWeek = state.value.date.atStartOfWeek()
        repeat(5) { i ->
            val date = startOfWeek.plusDays(i - 1L)
            val vPlanData = vPlanUseCases.getVPlanData(school!!, date)
            if (vPlanData.data == null) {
                Log.d("VPlanData $i", "null")
                _state.value = _state.value.copy(isLoading = false)
                return@repeat
            }
            vPlanUseCases.processVplanData(vPlanData.data)
            Log.d("VPlanData", vPlanData.toString())
        }
        _state.value = _state.value.copy(isLoading = false)
    }

    fun deletePlans(context: Context) {
        viewModelScope.launch {
            vPlanUseCases.deletePlans()
            init(context)
        }
    }

    fun onProfileSelected(context: Context, profileId: Long) {
        viewModelScope.launch {
            profileUseCases.setActiveProfile(profileId)
            init(context)
        }
    }

    fun setViewType(viewType: ViewType) {
        _state.value = _state.value.copy(viewMode = viewType)
    }

    fun setNotificationPermissionGranted(granted: Boolean) {
        _state.value = _state.value.copy(notificationPermissionGranted = granted)
    }
}

data class HomeState(
    val initDone: Boolean = false,
    val lessons: Map<LocalDate, List<Lesson>> = mapOf(),
    val isLoading: Boolean = false,
    val profiles: List<MenuProfile> = listOf(),
    val activeProfile: MenuProfile? = null,
    val date: LocalDate = LocalDate.now(),
    val viewMode: ViewType = ViewType.DAY,
    val notificationPermissionGranted: Boolean = false
)

enum class ViewType {
    WEEK, DAY
}
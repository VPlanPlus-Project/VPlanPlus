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
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import es.jvbabi.vplanplus.util.Worker
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val classUseCases: ClassUseCases,
    private val profileUseCases: ProfileUseCases,
    private val vPlanUseCases: VPlanUseCases,
    private val homeUseCases: HomeUseCases,
    private val keyValueUseCases: KeyValueUseCases,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val holidayRepository: HolidayRepository
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
        _state.value =
            _state.value.copy(activeProfile = activeProfile, lessons = mapOf())
        school = when (activeProfile.type) {
            ProfileType.STUDENT -> classUseCases.getClassById(activeProfile.referenceId).school
            ProfileType.TEACHER -> teacherRepository.getTeacherById(activeProfile.referenceId)!!.school
            ProfileType.ROOM -> roomRepository.getRoomById(activeProfile.referenceId).school
        }

        val syncDays = (keyValueUseCases.get(Keys.SETTINGS_SYNC_DAY_DIFFERENCE) ?: "3").toInt()

        if (!Worker.isWorkerRunning("SyncWork", context)) updateView(syncDays) // initial data

        viewModelScope.launch {
            Worker.isWorkerRunningFlow("SyncWork", context).collect {
                if (it != _state.value.syncing && !it) {
                    _state.value = _state.value.copy(syncing = false)
                    updateView(syncDays)
                } else _state.value = _state.value.copy(syncing = it)
            }
        }

        _state.value =
            _state.value.copy(
                profiles = profileUseCases.getProfiles().first(),
                initDone = true
            )
    }

    private suspend fun updateView(syncDays: Int) {
        repeat(syncDays + 2) { i ->
            val date = LocalDate.now().plusDays(i - 1L)
            _state.value = _state.value.copy(
                lessons = state.value.lessons.plus(
                    date to homeUseCases.getLessons(activeProfile, date).first()
                )
            )
        }
    }

    /**
     * Sets the day type for the given date.
     * Call only if theres no data for the given date
     * @param localDate Date to set the day type for
     */
    fun setDayType(localDate: LocalDate) {
        val dayType = holidayRepository.getDayType(school!!.schoolId, localDate)
        if (dayType == DayType.DATA) _state.value = _state.value.copy(
            lessons = state.value.lessons.plus(
                localDate to Day(
                    listOf(),
                    dayType = DayType.NO_DATA
                )
            )
        )
        else _state.value = _state.value.copy(
            lessons = state.value.lessons.plus(
                localDate to Day(
                    listOf(),
                    dayType
                )
            )
        )
    }

    fun getVPlanData(context: Context) {
        viewModelScope.launch {

            if (_state.value.syncing) {
                Log.d("HomeViewModel", "getVPlanData; already syncing")
                return@launch
            }

            val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("SyncWork")
                .addTag("ManualSyncWork")
                .build()
            WorkManager.getInstance(context).enqueue(syncWork)
        }
    }

    fun deletePlans() {
        viewModelScope.launch {
            vPlanUseCases.deletePlans()
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
    val lessons: Map<LocalDate, Day> = mapOf(),
    val isLoading: Boolean = false,
    val profiles: List<Profile> = listOf(),
    val activeProfile: Profile? = null,
    val date: LocalDate = LocalDate.now(),
    val viewMode: ViewType = ViewType.DAY,
    val notificationPermissionGranted: Boolean = false,
    val syncing: Boolean = false
)

enum class ViewType {
    WEEK, DAY
}

data class Day(
    val lessons: List<Lesson> = emptyList(),
    val dayType: DayType
)

enum class DayType {
    LOADING,
    NO_DATA,
    DATA,
    WEEKEND,
    HOLIDAY
}
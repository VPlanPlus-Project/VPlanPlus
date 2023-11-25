package es.jvbabi.vplanplus.ui.screens.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val vPlanUseCases: VPlanUseCases,
    private val keyValueRepository: KeyValueRepository,
    private val holidayRepository: HolidayRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val dataSyncJobs: HashMap<LocalDate, Job> = HashMap()

    private var startJob: Job? = null

    init {
        if (startJob == null) startJob = viewModelScope.launch {
            combine(
                profileUseCases.getProfiles(),
                keyValueRepository.getFlow(Keys.ACTIVE_PROFILE)
            ) { profiles, activeProfileId ->
                Log.d("HomeViewModel.MainFlow", "activeProfile ID: $activeProfileId")
                _state.value.copy(
                    profiles = profiles,
                    activeProfile = profiles.find { it.id == activeProfileId?.toLong() }
                )
            }.collect {
                _state.value = it
                updateView(state.value.date, 5)
                Log.d("VISIBLE DATE", state.value.date.toString())
            }
        }
    }

    fun onPageChanged(date: LocalDate) {
        updateView(date, 5)
        _state.value = _state.value.copy(date = date)
    }

    fun onInitPageChanged(date: LocalDate) {
        _state.value = _state.value.copy(initDate = date)
        onPageChanged(date)
    }

    private fun updateView(date: LocalDate, neighbors: Int) {
        if (dataSyncJobs.containsKey(date) || getActiveProfile() == null) return
        dataSyncJobs[date] = viewModelScope.launch {
            profileUseCases.getLessonsForProfile(getActiveProfile()!!, date).distinctUntilChanged().collect { day ->
                _state.value = _state.value.copy(
                    lessons = state.value.lessons.plus(
                        date to Day(
                            lessons = day.second,
                            dayType = day.first
                        )
                    )
                )
            }
        }
        repeat(neighbors/2) {
            updateView(date.plusDays(it.toLong()), 0)
            updateView(date.minusDays(it.toLong()), 0)
        }
    }

    /**
     * Sets the day type for the given date.
     * Call only if theres no data for the given date
     * @param localDate Date to set the day type for
     */
    fun setDayType(localDate: LocalDate) {
        if (_state.value.activeProfile == null) return
        viewModelScope.launch {
            val dayType = holidayRepository.getDayType(profileUseCases.getSchoolFromProfileId(_state.value.activeProfile!!.id).schoolId, localDate)
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

    fun onProfileSelected(profileId: Long) {
        Log.d("HomeViewMode.ChangedProfile", "onProfileSelected: $profileId")
        viewModelScope.launch {
            killUiSyncJobs()
            clearLessons()
            keyValueRepository.set(Keys.ACTIVE_PROFILE, profileId.toString())
        }
    }

    fun setViewType(viewType: ViewType) {
        _state.value = _state.value.copy(viewMode = viewType)
    }

    private fun getActiveProfile() = _state.value.activeProfile

    private fun clearLessons() {
        _state.value = _state.value.copy(lessons = mapOf())
    }

    private fun killUiSyncJobs() {
        dataSyncJobs.forEach { it.value.cancel() }
        dataSyncJobs.clear()
    }
}

data class HomeState(
    val lessons: Map<LocalDate, Day> = mapOf(),
    val isLoading: Boolean = false,
    val profiles: List<Profile> = listOf(),
    val activeProfile: Profile? = null,
    val initDate: LocalDate = LocalDate.now(),
    val date: LocalDate = LocalDate.now(),
    val viewMode: ViewType = ViewType.DAY,
    val notificationPermissionGranted: Boolean = false,
    val syncing: Boolean = false,
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
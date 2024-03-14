package es.jvbabi.vplanplus.feature.home.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.home.domain.usecase.Date
import es.jvbabi.vplanplus.feature.home.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases
) : ViewModel() {
    val state = mutableStateOf(HomeState())

    private var firstRun = true

    init {
        viewModelScope.launch oneTimeData@{
            val hints = homeUseCases.getVersionHintsUseCase()
            val versionName = BuildConfig.VERSION_NAME

            state.value = state.value.copy(
                versionHints = hints,
                isVersionHintsDialogOpen = hints.isNotEmpty(),
                currentVersion = versionName
            )
        }
        viewModelScope.launch {
            combine(
                listOf(
                    homeUseCases.getProfilesUseCase(),
                    homeUseCases.getCurrentIdentityUseCase(),
                    homeUseCases.getDayForCurrentProfileUseCase(Date.TODAY),
                    homeUseCases.getDayForCurrentProfileUseCase(Date.NEXT),
                    homeUseCases.getLastSyncUseCase(),
                    homeUseCases.getCurrentTimeUseCase(),
                    homeUseCases.getHomeworkUseCase(),
                    homeUseCases.isInfoExpandedUseCase(),
                    homeUseCases.isSyncRunningUseCase()
                )
            ) { data ->
                val profiles = data[0] as List<Profile>
                val currentIdentity = data[1] as Identity?
                val todayDay = data[2] as Day?
                val tomorrowDay = data[3] as Day?
                val lastSync = data[4] as ZonedDateTime?
                val time = data[5] as ZonedDateTime
                val userHomework = data[6] as List<Homework>
                val infoExpanded = data[7] as Boolean
                val syncing = data[8] as Boolean
                val bookings = homeUseCases.getRoomBookingsForTodayUseCase().filter {
                    it.`class`.classId == currentIdentity?.profile?.referenceId
                }

                var todayLessonExpanded = state.value.todayLessonExpanded
                if (firstRun) {
                    todayLessonExpanded =
                        todayDay?.anyLessonsLeft(time, currentIdentity!!.profile!!) ?: false
                }

                firstRun = false

                state.value.copy(
                    profiles = profiles,
                    currentIdentity = currentIdentity,
                    todayDay = todayDay,
                    nextDay = tomorrowDay,
                    lastSync = lastSync,
                    time = time,
                    userHomework = userHomework,
                    infoExpanded = infoExpanded,
                    todayLessonExpanded = todayLessonExpanded,
                    syncing = syncing,
                    bookings = bookings
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun onMenuOpenedChange(opened: Boolean) {
        state.value = state.value.copy(menuOpened = opened)
    }

    fun onInfoExpandChange(expanded: Boolean) {
        viewModelScope.launch {
            homeUseCases.setInfoExpandedUseCase(expanded)
        }
    }

    fun onTodayLessonExpandedToggle() {
        state.value = state.value.copy(todayLessonExpanded = !state.value.todayLessonExpanded)
    }

    fun switchProfile(to: Profile) {
        viewModelScope.launch {
            homeUseCases.changeProfileUseCase(to.id.toString())
        }
    }

    fun onRefreshClicked(context: Context) {
        viewModelScope.launch {
            if (state.value.syncing) {
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

    fun hideVersionHintsDialog(untilNextVersion: Boolean) {
        state.value = state.value.copy(isVersionHintsDialogOpen = false)
        if (untilNextVersion) viewModelScope.launch {
            homeUseCases.updateLastVersionHintsVersionUseCase()
        }
    }
}

data class HomeState(
    val profiles: List<Profile> = emptyList(),
    val currentIdentity: Identity? = null,
    val todayDay: Day? = null,
    val nextDay: Day? = null,
    val menuOpened: Boolean = false,
    val lastSync: ZonedDateTime? = null,
    val time: ZonedDateTime = ZonedDateTime.now(),
    val userHomework: List<Homework> = emptyList(),
    val infoExpanded: Boolean = false,
    val todayLessonExpanded: Boolean = true,
    val syncing: Boolean = false,

    val versionHints: List<VersionHints> = emptyList(),
    val isVersionHintsDialogOpen: Boolean = false,
    val currentVersion: String = "Loading...",

    val bookings: List<RoomBooking> = emptyList()
)
package es.jvbabi.vplanplus.feature.main_home.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.HomeUseCases
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.VppIdServer
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases
) : ViewModel() {
    var state by mutableStateOf(HomeState())

    init {
        viewModelScope.launch oneTimeData@{
            val hint = homeUseCases.getVersionHintsUseCase()
            val versionName = BuildConfig.VERSION_NAME

            state = state.copy(
                versionHint = hint,
                isVersionHintsDialogOpen = hint != null,
                currentVersion = versionName
            )
        }
        viewModelScope.launch {
            homeUseCases.getCurrentTimeUseCase().collect { state = state.copy(currentTime = it) }
        }
        viewModelScope.launch {
            startUiSync()
        }
        viewModelScope.launch {
            combine(
                listOf(
                    homeUseCases.getCurrentProfileUseCase(),
                    homeUseCases.getHomeworkUseCase(),
                    homeUseCases.isInfoExpandedUseCase(),
                    homeUseCases.getProfilesUseCase(),
                    homeUseCases.hasUnreadNewsUseCase(),
                    homeUseCases.isSyncRunningUseCase(),
                    homeUseCases.getLastSyncUseCase(),
                    homeUseCases.getHideFinishedLessonsUseCase(),
                    homeUseCases.hasInvalidVppIdSessionUseCase(),
                    homeUseCases.getVppIdServerUseCase(),
                    homeUseCases.hasMissingVppIdToProfileLinksUseCase(),
                    homeUseCases.getHolidaysUseCase()
                )
            ) { data ->
                val currentProfile = data[0] as Profile?
                val homework = data[1] as List<PersonalizedHomework>
                val infoExpanded = data[2] as Boolean
                val profiles = data[3] as List<Profile>
                val hasUnreadNews = data[4] as Boolean
                val syncing = data[5] as Boolean
                val lastSync = data[6] as ZonedDateTime
                val hideFinishedLessons = data[7] as Boolean
                val hasInvalidVppIdSession = data[8] as Boolean
                val server = data[9] as VppIdServer
                val hasMissingVppIdToProfileLinks = data[10] as Boolean
                val holidays = data[11] as List<LocalDate>

                val bookings = homeUseCases.getRoomBookingsForTodayUseCase().filter { it.bookedBy?.group?.groupId == (currentProfile as? ClassProfile)?.group?.groupId }

                state.copy(
                    currentProfile = currentProfile,
                    bookings = bookings,
                    homework = homework,
                    infoExpanded = infoExpanded,
                    profiles = profiles,
                    hasUnreadNews = hasUnreadNews,
                    isSyncRunning = syncing,
                    lastSync = lastSync,
                    hideFinishedLessons = hideFinishedLessons,
                    hasInvalidVppIdSession = hasInvalidVppIdSession,
                    server = server,
                    hasMissingVppIdToProfileLinks = hasMissingVppIdToProfileLinks,
                    holidays = holidays
                )
            }.collect {
                state = it
            }
        }
    }

    private fun startUiSync() {
        viewModelScope.launch {
            homeUseCases.getDayUseCase(LocalDate.now()).collect {
                state = state.copy(today = it)
            }
        }
    }

    fun onMenuOpenedChange(opened: Boolean) {
        state = state.copy(menuOpened = opened)
    }

    fun switchProfile(to: Profile) {
        onMenuOpenedChange(false)
        viewModelScope.launch {
            homeUseCases.changeProfileUseCase(to.id.toString())
        }
    }

    fun onRefreshClicked(context: Context) {
        viewModelScope.launch {
            if (state.isSyncRunning) {
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
        state = state.copy(isVersionHintsDialogOpen = false)
        if (untilNextVersion) viewModelScope.launch {
            homeUseCases.updateLastVersionHintsVersionUseCase()
        }
    }

    fun ignoreInvalidVppIdSessions() {
        viewModelScope.launch {
            homeUseCases.ignoreInvalidVppIdSessionsUseCase()
        }
    }
}

data class HomeState(
    val currentTime: ZonedDateTime = ZonedDateTime.now(),
    val currentProfile: Profile? = null,
    val today: SchoolDay? = null,
    val bookings: List<RoomBooking> = emptyList(),
    val homework: List<PersonalizedHomework> = emptyList(),
    val infoExpanded: Boolean = false,
    val menuOpened: Boolean = false,
    val profiles: List<Profile> = emptyList(),
    val hasUnreadNews: Boolean = false,
    val isSyncRunning: Boolean = false,
    val lastSync: ZonedDateTime? = null,
    val hideFinishedLessons: Boolean = false,
    val holidays: List<LocalDate> = emptyList(),

    val versionHint: VersionHints? = null,
    val isVersionHintsDialogOpen: Boolean = false,
    val currentVersion: String = "Loading...",

    val server: VppIdServer = servers.first(),

    val hasInvalidVppIdSession: Boolean = false,
    val hasMissingVppIdToProfileLinks: Boolean = false
)
package es.jvbabi.vplanplus.ui.screens.home.viewmodel

import android.app.Application
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
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Message
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.home.HomeUseCases
import es.jvbabi.vplanplus.domain.usecase.home.search.ResultGroup
import es.jvbabi.vplanplus.domain.usecase.home.search.SearchUseCases
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.Worker
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository,
    private val roomRepository: RoomRepository,
    private val timeRepository: TimeRepository,
    private val messageRepository: MessageRepository,
    private val homeUseCases: HomeUseCases,
    private val searchUseCases: SearchUseCases
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var dataSyncJob: MutableMap<LocalDate, Job?> = mutableMapOf()
    private var searchJob: Job? = null

    private var version = 0L

    private var homeUiSyncJob: Job? = null

    init {
        viewModelScope.launch {
            homeUseCases.getCurrentIdentity().collect {
                if (it == null) return@collect
                _state.value = _state.value.copy(
                    activeProfile = it.profile,
                    activeSchool = it.school,
                    currentVppId = it.vppId,
                    fullyCompatible = it.school?.fullyCompatible ?: false
                )
                if (it.school == null) return@collect
                var tomorrow = LocalDate.now().plusDays(1)
                while (tomorrow.dayOfWeek.value > it.school.daysPerWeek) {
                    tomorrow = tomorrow.plusDays(1)
                }
                _state.value = _state.value.copy(
                    nextDayDate = tomorrow
                )
                restartUiSync()
            }
        }
        if (homeUiSyncJob == null) homeUiSyncJob = viewModelScope.launch {
            combine(
                homeUseCases.getProfilesUseCase().distinctUntilChanged(),
                keyValueRepository.getFlow(Keys.LAST_SYNC_TS).distinctUntilChanged(),
                keyValueRepository.getFlow(Keys.LESSON_VERSION_NUMBER).distinctUntilChanged(),
                Worker.isWorkerRunningFlow("SyncWork", app.applicationContext)
                    .distinctUntilChanged(),
            ) { profiles, lastSyncTs, v, isSyncing ->
                version = v?.toLong() ?: 0
                _state.value.copy(
                    profiles = profiles.map { it.value }.flatten(),
                    lastSync = if (lastSyncTs != null) DateUtils.getDateTimeFromTimestamp(lastSyncTs.toLong()) else null,
                    syncing = isSyncing,
                )
            }.collect {
                _state.value = it
                restartUiSync()
            }
        }

        viewModelScope.launch {
            timeRepository.getTime().distinctUntilChanged().collect {
                _state.value = _state.value.copy(time = it)
            }
        }

        viewModelScope.launch {
            messageRepository.getUnreadMessages().distinctUntilChanged().collect {
                _state.value = _state.value.copy(unreadMessages = it)
            }
        }
    }

    /**
     * Starts the UI sync
     */
    private fun startLessonUiSync(force: Boolean, date: LocalDate) {
        if (dataSyncJob.containsKey(date) && !force) return
        if (force) dataSyncJob[date]?.cancel()
        dataSyncJob[date] = viewModelScope.launch {
            while (getActiveProfile() == null) delay(50)
            planRepository.getDayForProfile(getActiveProfile()!!, date, version)
                .distinctUntilChanged().collect { day ->
                    val bookings = roomRepository.getRoomBookings(date)
                    if (date.isEqual(LocalDate.now())) _state.value =
                        _state.value.copy(day = day, isLoading = false, bookings = bookings)
                    else _state.value =
                        _state.value.copy(nextDay = day, isLoading = false, bookings = bookings)
                }
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

    fun onProfileSelected(profileId: UUID) {
        Log.d("HomeViewMode.ChangedProfile", "onProfileSelected: $profileId")
        viewModelScope.launch {
            restartUiSync()
            clearLessons()
            keyValueRepository.set(Keys.ACTIVE_PROFILE, profileId.toString())
        }
    }

    private fun getActiveProfile() = _state.value.activeProfile

    private fun clearLessons() {
        _state.value = _state.value.copy(day = null)
    }

    // search
    fun onSearchOpened() {
        _state.value = _state.value.copy(searchOpen = true)
    }

    fun onSearchClosed() {
        _state.value =
            _state.value.copy(searchOpen = false, searchQuery = "", results = emptyList())
    }

    fun onSearchQueryUpdate(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        doSearch()
    }

    private fun doSearch(
        rawSelectedClassIds: List<UUID>? = null,
        rawSelectedTeacherIds: List<UUID>? = null,
        rawSelectedRoomIds: List<UUID>? = null
    ) {

        searchJob?.cancel()
        val query = _state.value.searchQuery
        if (query.isEmpty()) {
            _state.value = _state.value.copy(results = emptyList())
            return
        }

        val selectedClassIds = rawSelectedClassIds?: _state.value.results.mapNotNull { it.selectedClassId }
        val selectedTeacherIds = rawSelectedTeacherIds?: _state.value.results.mapNotNull { it.selectedTeacherId }
        val selectedRoomIds = rawSelectedRoomIds?: _state.value.results.mapNotNull { it.selectedRoomId }

        searchJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                results = searchUseCases.queryUseCase(
                    rawQuery = query,
                    selectedClassIds = selectedClassIds,
                    selectedTeacherIds = selectedTeacherIds,
                    selectedRoomIds = selectedRoomIds
                )
            )
        }
    }

    fun searchToggleFilter(type: SchoolEntityType) {
        _state.value = _state.value.copy(
            filter = _state.value.filter.plus(
                type to !(_state.value.filter[type] ?: true)
            )
        )
        doSearch()
    }

    fun selectSearchResult(type: SchoolEntityType, id: UUID) {
        doSearch(
            rawSelectedClassIds = if (type == SchoolEntityType.CLASS) _state.value.results.mapNotNull { it.selectedClassId } + id else null,
            rawSelectedTeacherIds = if (type == SchoolEntityType.TEACHER) _state.value.results.mapNotNull { it.selectedTeacherId } + id else null,
            rawSelectedRoomIds = if (type == SchoolEntityType.ROOM) _state.value.results.mapNotNull { it.selectedRoomId } + id else null
        )
    }

    private fun restartUiSync() {
        startLessonUiSync(true, _state.value.time.toLocalDate())
        startLessonUiSync(true, _state.value.nextDayDate)
    }
}

data class HomeState(
    val time: LocalDateTime = LocalDateTime.now(),
    val nextDayDate: LocalDate = LocalDate.now().plusDays(1),
    val day: Day? = null,
    val nextDay: Day? = null,
    val bookings: List<RoomBooking> = emptyList(),
    val isLoading: Boolean = true,
    val profiles: List<Profile> = listOf(),
    val activeProfile: Profile? = null,
    val activeSchool: School? = null,
    val currentVppId: VppId? = null,
    val notificationPermissionGranted: Boolean = false,
    val syncing: Boolean = false,
    val fullyCompatible: Boolean = true,
    val unreadMessages: List<Message> = emptyList(),

    // search
    val searchOpen: Boolean = false,
    val searchQuery: String = "",
    val filter: Map<SchoolEntityType, Boolean> = mapOf(
        SchoolEntityType.TEACHER to true,
        SchoolEntityType.ROOM to true,
        SchoolEntityType.CLASS to true,
    ),
    val results: List<ResultGroup> = emptyList(),


    val lastSync: LocalDateTime? = null
) {
    fun getActiveProfileDisplayName(): String {
        return if ((this.activeProfile?.displayName
                ?: "").length > 4
        ) this.activeProfile?.originalName ?: "" else this.activeProfile?.displayName
            ?: ""
    }
}
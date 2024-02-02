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
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Message
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.home.HomeUseCases
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.Worker
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val profileUseCases: ProfileUseCases,
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository,
    private val classUseCases: ClassUseCases,
    private val schoolUseCases: SchoolUseCases,
    private val lessonUseCases: LessonUseCases,
    private val keyValueUseCases: KeyValueUseCases,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val timeRepository: TimeRepository,
    private val messageRepository: MessageRepository,
    private val homeUseCases: HomeUseCases
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var dataSyncJob: Job? = null
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
                    fullyCompatible = it.school?.fullyCompatible?:false
                )
                startLessonUiSync(true)
            }
        }
        if (homeUiSyncJob == null) homeUiSyncJob = viewModelScope.launch {
            combine(
                profileUseCases.getProfiles().distinctUntilChanged(),
                keyValueRepository.getFlow(Keys.LAST_SYNC_TS).distinctUntilChanged(),
                keyValueRepository.getFlow(Keys.LESSON_VERSION_NUMBER).distinctUntilChanged(),
                Worker.isWorkerRunningFlow("SyncWork", app.applicationContext).distinctUntilChanged(),
            ) { profiles, lastSyncTs, v, isSyncing ->
                version = v?.toLong()?:0
                _state.value.copy(
                    profiles = profiles,
                    lastSync = if (lastSyncTs != null) DateUtils.getDateTimeFromTimestamp(lastSyncTs.toLong()) else null,
                    syncing = isSyncing,
                )
            }.collect {
                _state.value = it
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
    private fun startLessonUiSync(force: Boolean) {
        if (dataSyncJob != null && !force) return
        if (force) dataSyncJob?.cancel()
        dataSyncJob = viewModelScope.launch {
            planRepository.getDayForProfile(getActiveProfile()!!, LocalDate.now(), version).distinctUntilChanged().collect { day ->
                _state.value = _state.value.copy(day = day, isLoading = false)
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
            startLessonUiSync(true)
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
        _state.value = _state.value.copy(searchOpen = false, searchQuery = "", results = emptyList())
    }

    fun onSearchQueryUpdate(query: String) {
        _state.value = _state.value.copy(searchQuery = query)

        searchJob?.cancel()
        if (query.isEmpty()) {
            _state.value = _state.value.copy(results = emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            val schools = schoolUseCases.getSchools()
            val resultGroups = mutableListOf<ResultGroup>()
            schools.forEach { school ->

                var selectedTeacherId = _state.value.results.firstOrNull { it.school.schoolId == school.schoolId }?.selectedTeacherId
                var selectedRoomId = _state.value.results.firstOrNull { it.school.schoolId == school.schoolId }?.selectedRoomId
                var selectedClassId = _state.value.results.firstOrNull { it.school.schoolId == school.schoolId }?.selectedClassId

                val searchResult = mutableListOf<SearchResult>()
                if (_state.value.filter[SchoolEntityType.TEACHER]!!) {
                    val teachers = teacherRepository.getTeachersBySchoolId(school.schoolId).filter {
                        it.acronym.lowercase().contains(_state.value.searchQuery.lowercase())
                    }
                    teachers.forEachIndexed { index, teacher ->
                        if (selectedTeacherId == null && index == 0) selectedTeacherId = teacher.teacherId
                        val day = lessonUseCases.getLessonsForTeacher(
                            teacher, LocalDate.now(), keyValueUseCases.get(Keys.LESSON_VERSION_NUMBER)?.toLong() ?: 0
                        ).firstOrNull()
                        searchResult.add(
                            SearchResult(
                                id = teacher.teacherId,
                                name = teacher.acronym,
                                type = SchoolEntityType.TEACHER,
                                lessons = day?.lessons?: emptyList(),
                                detailed = teacher.teacherId == selectedTeacherId
                            )
                        )
                    }
                }
                if (state.value.filter[SchoolEntityType.ROOM]!!) {
                    val rooms = roomRepository.getRoomsBySchool(school).filter {
                        it.name.lowercase().contains(_state.value.searchQuery.lowercase())
                    }
                    rooms.forEachIndexed { index, room ->
                        if (selectedRoomId == null && index == 0) selectedRoomId = room.roomId
                        val day = lessonUseCases.getLessonsForRoom(
                            room,
                            LocalDate.now(),
                            keyValueUseCases.get(Keys.LESSON_VERSION_NUMBER)?.toLong() ?: 0
                        ).firstOrNull()
                        searchResult.add(
                            SearchResult(
                                id = room.roomId,
                                name = room.name,
                                type = SchoolEntityType.ROOM,
                                lessons = day?.lessons?: emptyList(),
                                detailed = room.roomId == selectedRoomId
                            )
                        )
                    }
                }
                if (state.value.filter[SchoolEntityType.CLASS]!!) {
                    val classes = classUseCases.getClassesBySchool(school).filter {
                        it.name.lowercase().contains(_state.value.searchQuery.lowercase())
                    }
                    classes.forEachIndexed { index, `class` ->
                        if (selectedClassId == null && index == 0) selectedClassId = `class`.classId
                        val day = lessonUseCases.getLessonsForClass(
                            `class`,
                            LocalDate.now(),
                            keyValueUseCases.get(Keys.LESSON_VERSION_NUMBER)?.toLong() ?: 0
                        ).firstOrNull()
                        searchResult.add(
                            SearchResult(
                                id = `class`.classId,
                                name = `class`.name,
                                type = SchoolEntityType.CLASS,
                                lessons = day?.lessons?: emptyList(),
                                detailed = `class`.classId == selectedClassId
                            )
                        )
                    }
                }

                resultGroups.add(ResultGroup(school, searchResult))
            }

            _state.value = _state.value.copy(results = resultGroups)
        }
    }

    private fun onSearchQueryUpdate() {
        onSearchQueryUpdate(_state.value.searchQuery)
    }

    fun searchToggleFilter(type: SchoolEntityType) {
        _state.value = _state.value.copy(
            filter = _state.value.filter.plus(
                type to !(_state.value.filter[type] ?: true)
            )
        )
        onSearchQueryUpdate()
    }

    fun selectSearchResult(schoolId: Long, type: SchoolEntityType, id: UUID) {
        val resultGroup = _state.value.results.firstOrNull { it.school.schoolId == schoolId } ?: return
        when (type) {
            SchoolEntityType.CLASS -> resultGroup.selectedClassId = id
            SchoolEntityType.TEACHER -> resultGroup.selectedTeacherId = id
            SchoolEntityType.ROOM -> resultGroup.selectedRoomId = id
        }
        _state.value = _state.value.copy(results = _state.value.results.map {
            if (it.school.schoolId == schoolId) resultGroup else it
        })
        onSearchQueryUpdate()
    }
}

data class HomeState(
    val time: LocalDateTime = LocalDateTime.now(),
    val day: Day? = null,
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

data class ResultGroup(
    val school: School,
    val searchResults: List<SearchResult>,
    var selectedClassId: UUID? = null,
    var selectedTeacherId: UUID? = null,
    var selectedRoomId: UUID? = null
)

data class SearchResult(
    val id: UUID,
    val name: String,
    val type: SchoolEntityType,
    val lessons: List<Lesson> = emptyList(),
    val detailed: Boolean = false
)
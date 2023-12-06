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
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
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
    private val vPlanUseCases: VPlanUseCases,
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository,
    private val classUseCases: ClassUseCases,
    private val schoolUseCases: SchoolUseCases,
    private val lessonUseCases: LessonUseCases,
    private val keyValueUseCases: KeyValueUseCases,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val dataSyncJobs: HashMap<LocalDate, Job> = HashMap()
    private var searchJob: Job? = null

    private var version = 0L

    private var homeUiSyncJob: Job? = null

    init {
        if (homeUiSyncJob == null) homeUiSyncJob = viewModelScope.launch {
            combine(
                profileUseCases.getProfiles().distinctUntilChanged(),
                keyValueRepository.getFlow(Keys.ACTIVE_PROFILE).distinctUntilChanged(),
                keyValueRepository.getFlow(Keys.LAST_SYNC_TS).distinctUntilChanged(),
                keyValueRepository.getFlow(Keys.LESSON_VERSION_NUMBER).distinctUntilChanged(),
                Worker.isWorkerRunningFlow("SyncWork", app.applicationContext).distinctUntilChanged()
            ) { profiles, activeProfileId, lastSyncTs, v, isSyncing ->
                version = v?.toLong()?:0
                _state.value.copy(
                    profiles = profiles,
                    activeProfile = profiles.find { it.id.toString() == activeProfileId },
                    lastSync = if (lastSyncTs != null) DateUtils.getDateTimeFromTimestamp(lastSyncTs.toLong()) else null,
                    syncing = isSyncing
                )
            }.collect {
                _state.value = it
                killUiSyncJobs()
                startLessonUiSync(state.value.date, 5)
            }
        }
    }

    /**
     * Called when the user changes the page
     * @param date The date of the new page
     */
    fun onPageChanged(date: LocalDate) {
        startLessonUiSync(date, 5)
        _state.value = _state.value.copy(date = date)
    }

    /**
     * Called when user clicks notification and gets redirected to specific page
     * @param date The date of the page to show
     */
    fun onInitPageChanged(date: LocalDate) {
        _state.value = _state.value.copy(initDate = date)
        onPageChanged(date)
    }

    /**
     * Starts the UI sync for the given date
     * @param date The date to sync
     * @param neighbors The number of neighbors to sync as well
     */
    private fun startLessonUiSync(date: LocalDate, neighbors: Int) {
        if (dataSyncJobs.containsKey(date) || getActiveProfile() == null) return
        dataSyncJobs[date] = viewModelScope.launch {
            planRepository.getDayForProfile(getActiveProfile()!!, date, version).distinctUntilChanged().collect { day ->
                _state.value = _state.value.copy(lessons = _state.value.lessons.plus(
                    date to day
                ))
            }
        }
        repeat(neighbors/2) {
            startLessonUiSync(date.plusDays(it.toLong()), 0)
            startLessonUiSync(date.minusDays(it.toLong()), 0)
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

    fun onProfileSelected(profileId: UUID) {
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
                val searchResult = mutableListOf<SearchResult>()
                if (_state.value.filter[FilterType.TEACHER]!!) {
                    val teachers = teacherRepository.getTeachersBySchoolId(school.schoolId).filter {
                        it.acronym.lowercase().contains(_state.value.searchQuery.lowercase())
                    }
                    val firstTeacher = teachers.firstOrNull()
                    if (firstTeacher != null) {
                        val day = lessonUseCases.getLessonsForTeacher(
                            firstTeacher, LocalDate.now(), keyValueUseCases.get(Keys.LESSON_VERSION_NUMBER)?.toLong() ?: 0
                        ).firstOrNull()
                        teachers.forEachIndexed { index, teacher ->
                            if (index == 0  && day != null && day.lessons.isNotEmpty() && day.state == DayDataState.DATA) {
                                searchResult.add(
                                    SearchResult(
                                        teacher.teacherId,
                                        teacher.acronym,
                                        FilterType.TEACHER,
                                        day.lessons
                                    )
                                )
                            } else {
                                searchResult.add(
                                    SearchResult(
                                        teacher.teacherId,
                                        teacher.acronym,
                                        FilterType.TEACHER
                                    )
                                )
                            }
                        }
                    }
                }
                if (state.value.filter[FilterType.ROOM]!!) {
                    val rooms = roomRepository.getRoomsBySchool(school).filter {
                        it.name.lowercase().contains(_state.value.searchQuery.lowercase())
                    }
                    val firstRoom = rooms.firstOrNull()
                    if (firstRoom != null) {
                        val day = lessonUseCases.getLessonsForRoom(
                            firstRoom,
                            LocalDate.now(),
                            keyValueUseCases.get(Keys.LESSON_VERSION_NUMBER)?.toLong() ?: 0
                        ).firstOrNull()
                        rooms.forEachIndexed { index, room ->
                            if (index == 0  && day != null && day.lessons.isNotEmpty() && day.state == DayDataState.DATA) {
                                searchResult.add(
                                    SearchResult(
                                        room.roomId,
                                        room.name,
                                        FilterType.ROOM,
                                        day.lessons
                                    )
                                )
                            } else {
                                searchResult.add(SearchResult(room.roomId, room.name, FilterType.ROOM))
                            }
                        }
                    }
                }
                if (state.value.filter[FilterType.CLASS]!!) {
                    val classes = classUseCases.getClassesBySchool(school).filter {
                        it.name.lowercase().contains(_state.value.searchQuery.lowercase())
                    }
                    val firstClass = classes.firstOrNull()
                    if (firstClass != null) {
                        val day = lessonUseCases.getLessonsForClass(
                            firstClass,
                            LocalDate.now(),
                            keyValueUseCases.get(Keys.LESSON_VERSION_NUMBER)?.toLong() ?: 0
                        )
                            .firstOrNull()
                        classes.forEachIndexed { index, `class` ->
                            if (index == 0  && day != null && day.lessons.isNotEmpty() && day.state == DayDataState.DATA) {
                                searchResult.add(
                                    SearchResult(
                                        `class`.classId,
                                        `class`.name,
                                        FilterType.CLASS,
                                        day.lessons
                                    )
                                )
                            } else {
                                searchResult.add(
                                    SearchResult(
                                        `class`.classId,
                                        `class`.name,
                                        FilterType.CLASS
                                    )
                                )
                            }
                        }
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

    fun searchToggleFilter(filterType: FilterType) {
        _state.value = _state.value.copy(
            filter = _state.value.filter.plus(
                filterType to !(_state.value.filter[filterType] ?: true)
            )
        )
        onSearchQueryUpdate()
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

    // search
    val searchOpen: Boolean = false,
    val searchQuery: String = "",
    val filter: Map<FilterType, Boolean> = mapOf(
        FilterType.TEACHER to true,
        FilterType.ROOM to true,
        FilterType.CLASS to true,
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

enum class ViewType {
    WEEK, DAY
}

data class ResultGroup(
    val school: School,
    val searchResults: List<SearchResult>
)

data class SearchResult(
    val id: UUID,
    val name: String,
    val type: FilterType,
    val lessons: List<Lesson> = emptyList()
)

enum class FilterType {
    TEACHER,
    ROOM,
    CLASS,
}
package es.jvbabi.vplanplus.feature.main_calendar.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.CalendarViewUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarViewUseCases: CalendarViewUseCases
) : ViewModel() {
    var state by mutableStateOf(CalendarViewState())
        private set

    private val uiSyncJobs = mutableMapOf<LocalDate, Job>()
    private var firstSyncStarted = false

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    calendarViewUseCases.getCurrentProfileUseCase(),
                    calendarViewUseCases.getLastSyncUseCase(),
                    calendarViewUseCases.canShowTimetableInfoBannerUseCase(),
                    calendarViewUseCases.getCurrentDataVersionUseCase()
                )
            ) { data ->
                val profile = data[0] as? Profile
                val lastSyncTimestamp = data[1] as? ZonedDateTime
                val canShowTimetableInfoBanner = data[2] as? Boolean ?: true
                val version = data[3] as? Int ?: 0

                if (profile == null) return@combine state
                state.copy(
                    currentProfile = profile,
                    lastSync = lastSyncTimestamp,
                    canShowTimetableInfoBanner = canShowTimetableInfoBanner,
                    version = if (state.version == -1) -1 else version
                )
            }.collect {
                state = it

                if (!firstSyncStarted) {
                    firstSyncStarted = true
                    startOneMonthAroundDate()
                }
            }
        }
    }

    fun doAction(action: CalendarViewAction) {
        viewModelScope.launch {
            when (action) {
                is CalendarViewAction.SelectDate -> {
                    state = state.copy(selectedDate = action.date, enabledFilters = emptyList())
                    startOneMonthAroundDate(action.date)
                }

                is CalendarViewAction.ToggleFilter -> {
                    val filters = state.enabledFilters.toMutableList()
                    if (filters.contains(action.filter)) {
                        filters.remove(action.filter)
                    } else {
                        filters.add(action.filter)
                    }
                    state = state.copy(enabledFilters = if (filters.size == DayViewFilter.entries.size) emptyList() else filters)
                }

                is CalendarViewAction.DismissTimetableInfoBanner -> {
                    calendarViewUseCases.dismissTimetableInfoBannerUseCase()
                }
            }
        }
    }

    private fun startOneMonthAroundDate(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            startDayUiSync(date)
            repeat(31) {
                startDayUiSync(date.plusDays(it.toLong()-31))
                startDayUiSync(date.plusDays(it.toLong()))
            }
        }
    }

    private fun startDayUiSync(date: LocalDate) {
        if (uiSyncJobs[date]?.isActive == true) return
        uiSyncJobs[date] = viewModelScope.launch {
            calendarViewUseCases.getDayUseCase(date).collect { day ->
                state = state.copy(
                    days = state.days + (date to day),
                    version = state.version.coerceAtLeast(0)
                )
            }
        }
    }
}

/**
 * @param currentSelectCause The cause of the current selection, if a click is detected, it will trigger all pagers to scroll to the selected date.
 */
data class CalendarViewState(
    val currentProfile: Profile? = null,
    val days: Map<LocalDate, SchoolDay> = emptyMap(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentSelectCause: DateSelectCause = DateSelectCause.CALENDAR_CLICK,
    val lastSync: ZonedDateTime? = null,
    val enabledFilters: List<DayViewFilter> = emptyList(),
    val canShowTimetableInfoBanner: Boolean = true,
    val version: Int = -1
)

sealed class CalendarViewAction {
    data class SelectDate(val date: LocalDate, val currentSelectCause: DateSelectCause) : CalendarViewAction()
    data class ToggleFilter(val filter: DayViewFilter) : CalendarViewAction()

    data object DismissTimetableInfoBanner : CalendarViewAction()
}

enum class DayViewFilter {
    GRADES, LESSONS, HOMEWORK, EXAMS
}

enum class DateSelectCause {
    CALENDAR_SWIPE,
    CALENDAR_CLICK,
    DAY_SWIPE,
}
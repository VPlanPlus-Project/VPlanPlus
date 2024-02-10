package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.CalendarEvent
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarRepository {
    fun getCalendars(): Flow<List<Calendar>>
    suspend fun getCalendarById(id: Long): Calendar?
    suspend fun insertEvent(event: CalendarEvent, school: School): Long?
    suspend fun deleteCalendarEvents(school: School, date: LocalDate)

    suspend fun processLessons(profile: Profile, day: Day)
}


package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getCalendars(): Flow<List<Calendar>>
    suspend fun getCalendarById(id: Long): Calendar?
    suspend fun insertEvent(event: CalendarEvent): Long?
    suspend fun getAppEvents(calendar: Calendar): List<CalendarEvent>
    suspend fun deleteEvent(id: Long)
    suspend fun deleteAppEvents(calendar: Calendar? = null)
}


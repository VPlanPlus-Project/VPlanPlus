package es.jvbabi.vplanplus.domain.repository

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getCalendars(): Flow<List<Calendar>>
    suspend fun getCalendarById(id: Long): Calendar?
    fun insertEvent(event: CalendarEvent): Uri?
}


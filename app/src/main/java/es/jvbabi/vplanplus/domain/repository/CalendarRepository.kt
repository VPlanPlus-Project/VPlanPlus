package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Calendar
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getCalendars(): Flow<List<Calendar>>
    suspend fun getCalendarById(id: Long): Calendar?
}


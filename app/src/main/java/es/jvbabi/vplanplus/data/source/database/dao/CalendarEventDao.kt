package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.DbCalendarEvent
import java.time.LocalDate

@Dao
abstract class CalendarEventDao {

    @Query("SELECT * FROM calendar_events WHERE date = :date AND schoolId = :schoolId")
    abstract suspend fun getCalendarEvents(date: LocalDate, schoolId: Long): List<DbCalendarEvent>

    @Insert
    abstract suspend fun insertCalendarEvent(calendarEvent: DbCalendarEvent): Long

    @Query("DELETE FROM calendar_events WHERE date = :date AND schoolId = :schoolId")
    abstract suspend fun deleteCalendarEvents(schoolId: Long, date: LocalDate)
}
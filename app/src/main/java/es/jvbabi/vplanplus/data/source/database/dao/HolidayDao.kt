package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Holiday
import java.time.LocalDate
import java.util.UUID

@Dao
abstract class HolidayDao {

    @Query("SELECT * FROM holiday WHERE school_id = :schoolId OR school_id IS NULL")
    abstract suspend fun getHolidaysBySchoolId(schoolId: Int): List<Holiday>

    @Query("INSERT OR REPLACE INTO holiday (id, school_id, date) VALUES (:id, :schoolId, :date)")
    abstract suspend fun insertHoliday(id: UUID = UUID.randomUUID(), schoolId: Int?, date: LocalDate)

    @Query("DELETE FROM holiday WHERE school_id = :schoolId")
    abstract suspend fun deleteHolidaysBySchoolId(schoolId: Int)

    @Query("SELECT * FROM holiday WHERE (school_id = :schoolId OR school_id IS NULL) AND date = :timestamp")
    abstract suspend fun find(schoolId: Int?, timestamp: LocalDate): Holiday?

    @Delete
    abstract suspend fun deleteHoliday(holiday: Holiday)
}
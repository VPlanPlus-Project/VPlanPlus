package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Holiday
import java.time.LocalDate

@Dao
abstract class HolidayDao {

    @Query("SELECT * FROM holiday WHERE schoolId = :schoolId OR schoolId IS NULL")
    abstract suspend fun getHolidaysBySchoolId(schoolId: Long): List<Holiday>

    @Insert
    abstract suspend fun insertHoliday(holiday: Holiday)

    @Query("DELETE FROM holiday WHERE schoolId = :schoolId")
    abstract suspend fun deleteHolidaysBySchoolId(schoolId: Long)

    @Query("SELECT * FROM holiday WHERE (schoolId = :schoolId OR schoolId IS NULL) AND date = :timestamp")
    abstract fun find(schoolId: Long?, timestamp: LocalDate): Holiday?

    @Delete
    abstract suspend fun deleteHoliday(holiday: Holiday)
}
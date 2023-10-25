package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Holiday

@Dao
abstract class HolidayDao {

    @Query("SELECT * FROM holiday WHERE schoolId = :schoolId OR schoolId IS NULL")
    abstract suspend fun getHolidaysBySchoolId(schoolId: String): List<Holiday>

    @Insert
    abstract suspend fun insertHoliday(holiday: Holiday)

    @Query("DELETE FROM holiday WHERE schoolId = :schoolId")
    abstract suspend fun deleteHolidaysBySchoolId(schoolId: String)
}
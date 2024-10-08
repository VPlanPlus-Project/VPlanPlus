package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbWeek
import es.jvbabi.vplanplus.data.model.DbWeekType
import es.jvbabi.vplanplus.data.model.combined.CWeek
import es.jvbabi.vplanplus.data.model.combined.CWeekType

@Dao
abstract class WeekDao {
    @Upsert
    abstract suspend fun upsertWeekType(weekType: DbWeekType)

    @Query("SELECT * FROM week_type WHERE school_id = :schoolId")
    abstract suspend fun getWeekTypesBySchool(schoolId: Int): List<CWeekType>

    @Upsert
    abstract suspend fun upsertWeek(week: DbWeek)

    @Query("SELECT * FROM week WHERE school_id = :schoolId")
    abstract suspend fun getWeeksBySchool(schoolId: Int): List<CWeek>

    @Query("DELETE FROM week")
    abstract suspend fun deleteAllWeeks()

    @Query("DELETE FROM week_type")
    abstract suspend fun deleteAllWeekTypes()
}
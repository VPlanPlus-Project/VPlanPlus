package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.sp24.SPlanInWeek

@Dao
abstract class SP24SPlanInWeekDao {
    @Upsert
    abstract fun upsertSPlanInWeek(sPlanInWeek: SPlanInWeek)

    @Query("SELECT * FROM sp24_splan_in_week WHERE school_id = :schoolId")
    abstract fun getIsSPlanInWeek(schoolId: Int): List<SPlanInWeek>
}
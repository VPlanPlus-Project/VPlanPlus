package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.data.model.combined.CPlanData
import java.time.LocalDate

@Dao
abstract class PlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPlan(plan: DbPlanData)

    @Transaction
    @Query("SELECT * FROM plan_data WHERE schoolId = :schoolId AND planDate = :date")
    abstract suspend fun getPlanByDate(schoolId: Long, date: LocalDate): CPlanData?

    @Query("SELECT planDate FROM plan_data")
    abstract suspend fun getLocalPlanDates(): List<LocalDate>

    @Query("DELETE FROM plan_data")
    abstract suspend fun deleteAllPlans()

    @Query("DELETE FROM plan_data WHERE version = :version")
    abstract suspend fun deleteAllPlansByVersion(version: Long)
}
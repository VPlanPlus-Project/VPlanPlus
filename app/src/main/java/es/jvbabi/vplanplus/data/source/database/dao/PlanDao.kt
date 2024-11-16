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
    @Query("SELECT * FROM plan_data WHERE school_id = :schoolId AND plan_date = :date")
    abstract suspend fun getPlanByDate(schoolId: Int, date: LocalDate): CPlanData?

    @Query("SELECT plan_date FROM plan_data")
    abstract suspend fun getLocalPlanDates(): List<LocalDate>

    @Query("DELETE FROM plan_data")
    abstract suspend fun deleteAllPlans()

    @Query("DELETE FROM plan_data WHERE version = :version")
    abstract suspend fun deleteAllPlansByVersion(version: Long)
}
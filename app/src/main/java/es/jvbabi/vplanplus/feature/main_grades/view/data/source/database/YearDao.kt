package es.jvbabi.vplanplus.feature.main_grades.view.data.source.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbInterval
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbYear
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.combined.CInterval

@Dao
abstract class YearDao {

    @Query("DELETE FROM bs_years")
    abstract fun dropAll()

    @Upsert
    abstract fun upsert(year: DbYear)

    @Upsert
    abstract fun upsertInterval(interval: DbInterval)

    @Query("SELECT * FROM bs_intervals WHERE id = :id")
    abstract fun getIntervalById(id: Long): CInterval
}
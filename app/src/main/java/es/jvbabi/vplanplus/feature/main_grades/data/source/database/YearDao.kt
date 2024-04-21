package es.jvbabi.vplanplus.feature.main_grades.data.source.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbInterval
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbYear

@Dao
abstract class YearDao {

    @Query("DELETE FROM bs_years")
    abstract fun dropAll()

    @Upsert
    abstract fun upsert(year: DbYear)

    @Upsert
    abstract fun upsertInterval(interval: DbInterval)
}
package es.jvbabi.vplanplus.feature.main_grades.data.source.database

import androidx.room.Dao
import androidx.room.Query
import java.time.LocalDate

@Dao
abstract class YearDao {

    @Query("DELETE FROM bs_years")
    abstract fun dropAll()

    @Query("INSERT OR REPLACE INTO bs_years (id, name, `from`, `to`) VALUES (:id, :name, :from, :to)")
    abstract fun upsert(id: Long, name: String, from: LocalDate, to: LocalDate)

    @Query("INSERT OR REPLACE INTO bs_intervals (id, name, type, `from`, `to`, includedIntervalId, yearId) VALUES (:id, :name, :type, :from, :to, :includedIntervalId, :yearId)")
    abstract fun upsertInterval(id: Long, name: String, type: String, from: LocalDate, to: LocalDate, includedIntervalId: Long?, yearId: Long)
}
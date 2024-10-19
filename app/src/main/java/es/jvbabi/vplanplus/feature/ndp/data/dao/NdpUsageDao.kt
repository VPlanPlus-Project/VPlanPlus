package es.jvbabi.vplanplus.feature.ndp.data.dao

import androidx.room.Dao
import androidx.room.Query
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Dao
abstract class NdpUsageDao {
    @Query("INSERT OR REPLACE INTO ndp_profile_time (profile_id, date, time, has_completed) VALUES (:profileId, :date, :time, :hasCompleted)")
    abstract suspend fun insert(profileId: UUID, date: LocalDate, time: LocalTime, hasCompleted: Boolean)

    @Query("SELECT has_completed FROM ndp_profile_time WHERE profile_id = :profileId AND date = :date LIMIT 1")
    abstract suspend fun isNdpFinished(profileId: UUID, date: LocalDate): Boolean?

    @Query("UPDATE ndp_profile_time SET has_completed = 1 WHERE profile_id = :profileId AND date = :date")
    abstract suspend fun finishNdp(profileId: UUID, date: LocalDate)
}
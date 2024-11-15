package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import es.jvbabi.vplanplus.data.model.DbAlarm
import java.time.ZonedDateTime

@Dao
abstract class AlarmDao {
    @Query("INSERT OR REPLACE INTO alarm (time, tags, data) VALUES (:time, :tags, :data)")
    abstract suspend fun insert(time: ZonedDateTime, tags: String, data: String): Long

    @Query("SELECT * FROM alarm WHERE id = :id")
    abstract suspend fun getAlarmById(id: Int): DbAlarm?

    @Query("SELECT * FROM alarm")
    abstract suspend fun getAlarms(): List<DbAlarm>

    @Query("DELETE FROM alarm WHERE id = :id")
    abstract suspend fun delete(id: Int)

    @Query("DELETE FROM alarm WHERE time < :currentTime")
    abstract suspend fun deleteOld(currentTime: ZonedDateTime)
}
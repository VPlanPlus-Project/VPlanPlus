package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import es.jvbabi.vplanplus.feature.homework.shared.data.model.DbPreferredNotificationTime
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PreferredHomeworkNotificationTimeDao {

    @Query("SELECT * FROM preferred_notification_time")
    abstract fun getPreferredHomeworkNotificationTime(): Flow<List<DbPreferredNotificationTime>>

    @Query("DELETE FROM preferred_notification_time WHERE day_of_week = :dayOfWeek")
    abstract suspend fun deletePreferredHomeworkNotificationTime(dayOfWeek: Int)

    @Query("INSERT INTO preferred_notification_time (day_of_week, hour, minute, override_default) VALUES (:dayOfWeek, :hour, :minute, :overrideDefault)")
    abstract suspend fun insertPreferredHomeworkNotificationTime(dayOfWeek: Int, hour: Int, minute: Int, overrideDefault: Boolean)
}
package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.feature.main_homework.shared.data.model.DbPreferredNotificationTime
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PreferredHomeworkNotificationTimeDao {

    @Query("SELECT * FROM preferred_notification_time")
    abstract fun getPreferredHomeworkNotificationTime(): Flow<List<DbPreferredNotificationTime>>

    @Query("DELETE FROM preferred_notification_time WHERE day_of_week = :dayOfWeek")
    abstract suspend fun deletePreferredHomeworkNotificationTime(dayOfWeek: Int)

    @Upsert
    abstract suspend fun insertPreferredHomeworkNotificationTime(data: DbPreferredNotificationTime)
}
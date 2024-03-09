package es.jvbabi.vplanplus.feature.homework.shared.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.PreferredHomeworkNotificationTime
import java.time.DayOfWeek

@Entity(
    tableName = "preferred_notification_time",
    primaryKeys = ["day_of_week"]
)
data class DbPreferredNotificationTime(
    @ColumnInfo("day_of_week") val dayOfWeek: Int,
    @ColumnInfo("hour") val hour: Int,
    @ColumnInfo("minute") val minute: Int,
    @ColumnInfo("override_default") val overrideDefault: Boolean
) {
    fun toModel(): PreferredHomeworkNotificationTime {
        return PreferredHomeworkNotificationTime(
            dayOfWeek = DayOfWeek.of(dayOfWeek),
            hour = hour,
            minute = minute,
            overrideDefault = overrideDefault
        )
    }
}
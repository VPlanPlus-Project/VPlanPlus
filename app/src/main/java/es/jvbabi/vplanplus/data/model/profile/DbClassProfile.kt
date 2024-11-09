package es.jvbabi.vplanplus.data.model.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.vppid.DbVppId
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import java.util.UUID

@Entity(
    tableName = "profile_class",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["class_id"]),
        Index(value = ["vpp_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["class_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DbVppId::class,
            parentColumns = ["id"],
            childColumns = ["vpp_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class DbClassProfile(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("name") val name: String,
    @ColumnInfo("custom_name") val customName: String,
    @ColumnInfo("calendar_mode") val calendarMode: ProfileCalendarType,
    @ColumnInfo("calendar_id") val calendarId: Long?,
    @ColumnInfo("class_id") val classId: Int,
    @ColumnInfo("is_homework_enabled") val isHomeworkEnabled: Boolean,
    @ColumnInfo("is_daily_notification_enabled", defaultValue = "false") val isDailyNotificationEnabled: Boolean,
    @ColumnInfo("vpp_id") val vppId: Int?
)

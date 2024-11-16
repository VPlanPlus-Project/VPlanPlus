package es.jvbabi.vplanplus.data.model.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import java.util.UUID

@Entity(
    tableName = "profile_teacher",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["teacher_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class DbTeacherProfile(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("name") val name: String,
    @ColumnInfo("custom_name") val customName: String,
    @ColumnInfo("calendar_mode") val calendarMode: ProfileCalendarType,
    @ColumnInfo("calendar_id") val calendarId: Long?,
    @ColumnInfo("teacher_id") val teacherId: UUID,
    @ColumnInfo("is_notifications_enabled", defaultValue = "true") val isNotificationsEnabled: Boolean,
    @ColumnInfo("notification_settings", defaultValue = "[]") val notificationSettings: String,
)
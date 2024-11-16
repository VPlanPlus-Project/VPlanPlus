package es.jvbabi.vplanplus.data.model.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import java.util.UUID

@Entity(
    tableName = "profile_room",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["room_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbRoom::class,
            parentColumns = ["id"],
            childColumns = ["room_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class DbRoomProfile(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("name") val name: String,
    @ColumnInfo("custom_name") val customName: String,
    @ColumnInfo("calendar_mode") val calendarMode: ProfileCalendarType,
    @ColumnInfo("calendar_id") val calendarId: Long?,
    @ColumnInfo("room_id") val roomId: Int,
)
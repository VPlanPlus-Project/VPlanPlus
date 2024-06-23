package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
    tableName = "room_booking",
    primaryKeys = ["id", "room_id", "booked_by", "from", "to"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["room_id"]),
        Index(value = ["booked_by"]),
        Index(value = ["group_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["room_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbVppId::class,
            parentColumns = ["id"],
            childColumns = ["booked_by"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbRoomBooking(
    @ColumnInfo("id") val id: Long,
    @ColumnInfo("room_id") val roomId: UUID,
    @ColumnInfo("booked_by") val bookedBy: Int,
    @ColumnInfo("from") val from: ZonedDateTime,
    @ColumnInfo("to") val to: ZonedDateTime,
    @ColumnInfo("group_id") val groupId: Int
)

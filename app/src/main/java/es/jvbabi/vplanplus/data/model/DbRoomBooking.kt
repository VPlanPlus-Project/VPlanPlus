package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.ZonedDateTime

@Entity(
    tableName = "room_booking",
    primaryKeys = ["id", "room_id", "booked_by", "from", "to"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["room_id"]),
        Index(value = ["booked_by"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbRoom::class,
            parentColumns = ["id"],
            childColumns = ["room_id"],
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
    @ColumnInfo("room_id") val roomId: Int,
    @ColumnInfo("booked_by") val bookedBy: Int,
    @ColumnInfo("from") val from: ZonedDateTime,
    @ColumnInfo("to") val to: ZonedDateTime,
)

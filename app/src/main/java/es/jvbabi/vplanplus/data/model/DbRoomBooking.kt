package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
    tableName = "room_booking",
    primaryKeys = ["id", "roomId", "bookedBy", "from", "to"],
    indices = [
        Index(value = ["id"], unique = true),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["class"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbRoomBooking(
    val id: Long,
    val roomId: UUID,
    val bookedBy: Int,
    val from: ZonedDateTime,
    val to: ZonedDateTime,
    val `class`: UUID
)

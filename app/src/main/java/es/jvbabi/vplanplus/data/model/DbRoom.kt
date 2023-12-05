package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.School
import java.util.UUID

@Entity(
    tableName = "room",
    primaryKeys = ["roomId"],
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolRoomRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["roomId"], unique = true),
        Index(value = ["schoolRoomRefId"]),
    ]
)
data class DbRoom(
    val roomId: UUID = UUID.randomUUID(),
    val schoolRoomRefId: Long,
    val name: String,
)
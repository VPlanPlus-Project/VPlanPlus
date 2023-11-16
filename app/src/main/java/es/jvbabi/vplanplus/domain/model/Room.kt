package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "room",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolRoomRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Room(
    @PrimaryKey(autoGenerate = true) val roomId: Long = 0,
    val schoolRoomRefId: Long,
    val name: String,
)
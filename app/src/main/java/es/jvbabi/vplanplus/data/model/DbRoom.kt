package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import es.jvbabi.vplanplus.domain.model.School

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
data class DbRoom(
    @PrimaryKey(autoGenerate = true) val roomId: Long = 0,
    val schoolRoomRefId: Long,
    val name: String,
)
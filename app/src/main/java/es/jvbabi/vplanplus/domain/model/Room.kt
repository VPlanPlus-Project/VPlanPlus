package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "room",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Room(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val schoolId: Int,
    val name: String,
)
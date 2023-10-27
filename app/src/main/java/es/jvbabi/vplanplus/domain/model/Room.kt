package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "room",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["id"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Room(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val schoolId: Long,
    val name: String,
)
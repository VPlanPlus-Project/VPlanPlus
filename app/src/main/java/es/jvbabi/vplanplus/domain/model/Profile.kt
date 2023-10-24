package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profile",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val type: Int, // TODO change to enum; 0 student, 1 teacher, 2 room
    val name: String,
    val referenceId: Int, // can be class id, teacher id or room id
)
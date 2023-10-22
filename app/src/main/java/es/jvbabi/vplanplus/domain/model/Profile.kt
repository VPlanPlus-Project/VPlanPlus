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
    @PrimaryKey(autoGenerate = true) val id: Int,
    val type: Int, // TODO change to enum
    val name: String,
    val reference: String, // can be class name, teacher short or room number
)
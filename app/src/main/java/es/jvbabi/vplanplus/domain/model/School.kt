package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "school",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class School(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val password: String,
)
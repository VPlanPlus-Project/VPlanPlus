package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "school",
)
data class School(
    @PrimaryKey val id: Long? = null,
    val name: String,
    val username: String,
    val password: String,
    val daysPerWeek: Int,
)
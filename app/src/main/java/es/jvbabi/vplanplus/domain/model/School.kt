package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "school",
)
data class School(
    @PrimaryKey val schoolId: Long = 0,
    val name: String,
    val username: String,
    val password: String,
    val daysPerWeek: Int
)
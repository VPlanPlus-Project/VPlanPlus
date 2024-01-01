package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "school",
    primaryKeys = ["schoolId"],
    indices = [
        Index(value = ["schoolId"], unique = true),
    ]
)
data class School(
    val schoolId: Long,
    val name: String,
    val username: String,
    val password: String,
    val daysPerWeek: Int,
    val fullyCompatible: Boolean
)
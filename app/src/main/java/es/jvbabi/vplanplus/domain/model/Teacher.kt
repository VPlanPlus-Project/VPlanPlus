package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "teacher",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val acronym: String,
    val schoolId: String,
)

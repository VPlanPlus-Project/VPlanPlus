package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "default_lesson"
)
data class DefaultLesson(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val schoolId: String,
    val vpId: Int,
    val teacherId: Int,
    val subject: String,
)
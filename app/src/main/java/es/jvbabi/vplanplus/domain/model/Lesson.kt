package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lesson",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val lesson: Int,
    val subject: String,
    val classId: String,
    val teacherId: String,
    val roomId: String,
)
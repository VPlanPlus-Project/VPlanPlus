package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "default_lesson",
    primaryKeys = ["defaultLessonId"],
    indices = [
        Index(value = ["defaultLessonId"], unique = true),
        Index(value = ["vpId"], unique = true),
    ]
)
data class DbDefaultLesson(
    val defaultLessonId: UUID,
    val vpId: Long,
    val subject: String,
    val teacherId: Long?,
    val classId: Long,
)
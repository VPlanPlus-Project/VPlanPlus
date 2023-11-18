package es.jvbabi.vplanplus.domain.model

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
data class DefaultLesson(
    val defaultLessonId: UUID,
    val vpId: Long,
    val subject: String,
    val teacherId: Long?,
    val classId: Long,
)

package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "default_lesson",
    primaryKeys = ["defaultLessonId"],
    indices = [
        Index(value = ["defaultLessonId"], unique = true)
    ]
)
data class DefaultLesson(
    val defaultLessonId: Long,
    val vpId: Long,
    val subject: String,
    val teacherId: Long?,
    val classId: Long,
)

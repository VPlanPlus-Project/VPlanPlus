package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "default_lesson",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class DefaultLesson(
    val id: Long,
    val vpId: Long,
    val subject: String,
    val teacherId: Long?,
    val classId: Long,
)

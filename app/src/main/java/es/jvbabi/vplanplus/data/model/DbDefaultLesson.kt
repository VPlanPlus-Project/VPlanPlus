package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "default_lesson",
    primaryKeys = ["defaultLessonId"],
    indices = [
        Index(value = ["defaultLessonId"], unique = true),
        Index(value = ["vpId"]),
        Index(value = ["classId"]),
        Index(value = ["teacherId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class DbDefaultLesson(
    val defaultLessonId: UUID,
    val vpId: Long,
    val subject: String,
    val teacherId: UUID?,
    val classId: UUID,
)
package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "lesson",
    foreignKeys = [
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["classLessonRefId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    primaryKeys = ["lessonId"],
    indices = [
        Index(value = ["classLessonRefId"]),
        Index(value = ["lessonId"], unique = true)
    ]
)
data class DbLesson(
    val lessonId: UUID = UUID.randomUUID(),
    val lessonNumber: Int,
    val changedSubject: String?,
    val classLessonRefId: UUID,
    val defaultLessonId: UUID?,
    val info: String?,
    val roomIsChanged: Boolean,
    val day: LocalDate,
    val version: Long
)

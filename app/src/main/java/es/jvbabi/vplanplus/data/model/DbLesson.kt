package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "lesson",
    foreignKeys = [
        ForeignKey(
            entity = DbClass::class,
            parentColumns = ["classId"],
            childColumns = ["classLessonRefId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    primaryKeys = ["lessonId"]
)
data class DbLesson(
    val lessonId: UUID = UUID.randomUUID(),
    val lessonNumber: Int,
    val changedSubject: String?,
    val classLessonRefId: Long,
    val defaultLessonId: UUID?,
    val info: String?,
    val roomIsChanged: Boolean,
    val day: LocalDate,
    val version: Long
)

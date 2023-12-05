package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbTeacher
import java.util.UUID

@Entity(
    tableName = "lesson_teacher_crossover",
    primaryKeys = ["ltcLessonId", "ltcTeacherId"],
    foreignKeys = [
        ForeignKey(
            entity = DbLesson::class,
            parentColumns = ["lessonId"],
            childColumns = ["ltcLessonId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbTeacher::class,
            parentColumns = ["teacherId"],
            childColumns = ["ltcTeacherId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["ltcLessonId"]),
        Index(value = ["ltcTeacherId"]),
    ]
)
data class LessonTeacherCrossover(
    val ltcLessonId: UUID,
    val ltcTeacherId: UUID
)
package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.Entity
import androidx.room.ForeignKey
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.domain.model.Teacher

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
            entity = Teacher::class,
            parentColumns = ["teacherId"],
            childColumns = ["ltcTeacherId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class LessonTeacherCrossover(
    val ltcLessonId: Long,
    val ltcTeacherId: Long
)
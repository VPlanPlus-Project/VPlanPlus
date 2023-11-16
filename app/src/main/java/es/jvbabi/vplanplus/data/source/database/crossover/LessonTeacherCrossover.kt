package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.domain.model.Teacher

@Entity(
    tableName = "lesson_teacher_crossover",
    foreignKeys = [
        ForeignKey(
            entity = DbLesson::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Teacher::class,
            parentColumns = ["id"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class LessonTeacherCrossover(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val lessonId: Long,
    val teacherId: Long
)
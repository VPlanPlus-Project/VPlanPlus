package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
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
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["ltcTeacherId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["ltcLessonId"]),
        Index(value = ["ltcSchoolEntityId"]),
    ]
)
data class LessonSchoolEntityCrossover(
    val ltcLessonId: UUID,
    val ltcSchoolEntityId: UUID
)
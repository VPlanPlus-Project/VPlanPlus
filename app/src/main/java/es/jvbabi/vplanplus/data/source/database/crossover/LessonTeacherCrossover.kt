package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import java.util.UUID

@Entity(
    tableName = "lesson_teacher_crossover",
    primaryKeys = ["lesson_id", "school_entity_id"],
    indices = [
        Index(value = ["lesson_id"]),
        Index(value = ["school_entity_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbLesson::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["school_entity_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class LessonTeacherCrossover(
    @ColumnInfo("lesson_id") val lessonId: UUID,
    @ColumnInfo("school_entity_id") val schoolEntityId: UUID
)
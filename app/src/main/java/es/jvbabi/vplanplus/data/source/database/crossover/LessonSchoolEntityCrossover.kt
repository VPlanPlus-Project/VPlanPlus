package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import java.util.UUID

@Entity(
    tableName = "lesson_se_crossover",
    primaryKeys = ["lsecLessonId", "lsecSchoolEntityId"],
    foreignKeys = [
        ForeignKey(
            entity = DbLesson::class,
            parentColumns = ["lessonId"],
            childColumns = ["lsecLessonId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["lsecSchoolEntityId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["lsecLessonId"]),
        Index(value = ["lsecSchoolEntityId"]),
    ]
)
data class LessonSchoolEntityCrossover(
    val lsecLessonId: UUID,
    val lsecSchoolEntityId: UUID
)
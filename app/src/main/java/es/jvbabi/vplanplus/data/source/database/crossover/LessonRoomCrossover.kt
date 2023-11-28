package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.Entity
import androidx.room.ForeignKey
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbRoom
import java.util.UUID

@Entity(
    tableName = "lesson_room_crossover",
    primaryKeys = ["lrcLessonId", "lrcRoomId"],
    foreignKeys = [
        ForeignKey(
            entity = DbLesson::class,
            parentColumns = ["lessonId"],
            childColumns = ["lrcLessonId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbRoom::class,
            parentColumns = ["roomId"],
            childColumns = ["lrcRoomId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class LessonRoomCrossover(
    val lrcLessonId: UUID,
    val lrcRoomId: Long
)
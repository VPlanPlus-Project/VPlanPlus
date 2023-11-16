package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.domain.model.Room

@Entity(
    tableName = "lesson_room_crossover",
    foreignKeys = [
        ForeignKey(
            entity = DbLesson::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Room::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class LessonRoomCrossover(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val lessonId: Long,
    val roomId: Long
)
package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbRoom
import java.util.UUID

@Entity(
    tableName = "lesson_room_crossover",
    primaryKeys = ["lesson_id", "room_id"],
    indices = [
        Index(value = ["lesson_id"]),
        Index(value = ["room_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbLesson::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbRoom::class,
            parentColumns = ["id"],
            childColumns = ["room_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class LessonRoomCrossover(
    @ColumnInfo("lesson_id") val lessonId: UUID,
    @ColumnInfo("room_id") val roomId: Int
)
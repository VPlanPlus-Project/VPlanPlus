package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import java.util.UUID

@Dao
abstract class LessonRoomCrossoverDao {
    @Query("DELETE FROM lesson_room_crossover WHERE lrcLessonId = :lessonId")
    abstract suspend fun deleteCrossoversByLessonId(lessonId: UUID)

    @Query("INSERT INTO lesson_room_crossover (lrcLessonId, lrcRoomId) VALUES (:lessonId, :roomId)")
    abstract suspend fun insertCrossover(lessonId: UUID, roomId: Long)

    @Transaction
    open suspend fun insertCrossovers(crossovers: List<Pair<UUID, Long>>) {
        crossovers.forEach {
            insertCrossover(it.first, it.second)
        }
    }
}
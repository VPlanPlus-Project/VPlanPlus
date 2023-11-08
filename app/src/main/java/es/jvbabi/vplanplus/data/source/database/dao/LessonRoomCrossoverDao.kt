package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class LessonRoomCrossoverDao {

    @Query("SELECT roomId FROM lesson_room_crossover WHERE lessonId = :lessonId")
    abstract suspend fun getRoomIdsByLessonId(lessonId: Long): List<Long>

    @Query("DELETE FROM lesson_room_crossover WHERE lessonId = :lessonId")
    abstract suspend fun deleteCrossoversByLessonId(lessonId: Long)

    @Query("INSERT INTO lesson_room_crossover (lessonId, roomId) VALUES (:lessonId, :roomId)")
    abstract suspend fun insertCrossover(lessonId: Long, roomId: Long)
}
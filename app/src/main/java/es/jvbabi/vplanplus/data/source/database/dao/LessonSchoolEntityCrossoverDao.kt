package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import java.util.UUID

@Dao
abstract class LessonSchoolEntityCrossoverDao {

    @Query("DELETE FROM lesson_teacher_crossover WHERE lesson_id = :lessonId")
    abstract suspend fun deleteTeacherCrossoversByLessonId(lessonId: UUID)

    @Query("DELETE FROM lesson_room_crossover WHERE lesson_id = :lessonId")
    abstract suspend fun deleteRoomCrossoversByLessonId(lessonId: UUID)

    @Query("INSERT INTO lesson_teacher_crossover (lesson_id, school_entity_id) VALUES (:lessonId, :teacherId)")
    abstract suspend fun insertTeacherCrossover(lessonId: UUID, teacherId: UUID)

    @Query("INSERT INTO lesson_room_crossover (lesson_id, room_id) VALUES (:lessonId, :roomId)")
    abstract suspend fun insertRoomCrossover(lessonId: UUID, roomId: Int)

    /**
     * Inserts a list of crossovers into the database.
     * @param crossovers A list of pairs of UUIDs, where the first UUID is the lessonId and the second UUID is the schoolEntityId.
     */
    @Transaction
    open suspend fun insertTeacherCrossovers(crossovers: List<Pair<UUID, UUID>>) {
        crossovers.forEach {
            insertTeacherCrossover(it.first, it.second)
        }
    }

    /**
     * Inserts a list of crossovers into the database.
     * @param crossovers A list of pairs of UUID to Int, where the first UUID is the lessonId and the second Int is the roomId.
     */
    @Transaction
    open suspend fun insertRoomCrossovers(crossovers: List<Pair<UUID, Int>>) {
        crossovers.forEach {
            insertRoomCrossover(it.first, it.second)
        }
    }
}
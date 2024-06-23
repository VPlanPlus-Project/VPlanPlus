package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import java.util.UUID

@Dao
abstract class LessonSchoolEntityCrossoverDao {

    @Query("DELETE FROM lesson_se_crossover WHERE lesson_id = :lessonId")
    abstract suspend fun deleteCrossoversByLessonId(lessonId: UUID)

    @Query("INSERT INTO lesson_se_crossover (lesson_id, school_entity_id) VALUES (:lessonId, :teacherId)")
    abstract suspend fun insertCrossover(lessonId: UUID, teacherId: UUID)

    /**
     * Inserts a list of crossovers into the database.
     * @param crossovers A list of pairs of UUIDs, where the first UUID is the lessonId and the second UUID is the schoolEntityId.
     */
    @Transaction
    open suspend fun insertCrossovers(crossovers: List<Pair<UUID, UUID>>) {
        crossovers.forEach {
            insertCrossover(it.first, it.second)
        }
    }
}
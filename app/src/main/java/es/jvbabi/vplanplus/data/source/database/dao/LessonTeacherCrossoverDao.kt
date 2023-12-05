package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import java.util.UUID

@Dao
abstract class LessonTeacherCrossoverDao {

    @Query("DELETE FROM lesson_teacher_crossover WHERE ltcLessonId = :lessonId")
    abstract suspend fun deleteCrossoversByLessonId(lessonId: UUID)

    @Query("INSERT INTO lesson_teacher_crossover (ltcLessonId, ltcTeacherId) VALUES (:lessonId, :teacherId)")
    abstract suspend fun insertCrossover(lessonId: UUID, teacherId: UUID)

    @Transaction
    open suspend fun insertCrossovers(crossovers: List<Pair<UUID, UUID>>) {
        crossovers.forEach {
            insertCrossover(it.first, it.second)
        }
    }
}
package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class LessonTeacherCrossoverDao {

    @Query("SELECT ltcTeacherId FROM lesson_teacher_crossover WHERE ltcLessonId = :lessonId")
    abstract suspend fun getTeacherIdsByLessonId(lessonId: Long): List<Long>

    @Query("DELETE FROM lesson_teacher_crossover WHERE ltcLessonId = :lessonId")
    abstract suspend fun deleteCrossoversByLessonId(lessonId: Long)

    @Query("INSERT INTO lesson_teacher_crossover (ltcLessonId, ltcTeacherId) VALUES (:lessonId, :teacherId)")
    abstract suspend fun insertCrossover(lessonId: Long, teacherId: Long)
}
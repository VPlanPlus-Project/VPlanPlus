package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LessonDao {
    @Query("SELECT * FROM lesson WHERE classId = :classId AND dayTimestamp = :timestamp")
    abstract fun getLessonsByClass(classId: Long, timestamp: Long): Flow<List<Lesson>>

    @Query("SELECT lesson.* FROM lesson LEFT JOIN lesson_teacher_crossover ON lesson.id = lesson_teacher_crossover.lessonId WHERE lesson_teacher_crossover.teacherId = :teacherId AND lesson.dayTimestamp = :timestamp")
    abstract fun getLessonsByTeacher(teacherId: Long, timestamp: Long): Flow<List<Lesson>>

    @Query("SELECT lesson.* FROM lesson LEFT JOIN lesson_room_crossover ON lesson.id = lesson_room_crossover.lessonId WHERE lesson_room_crossover.roomId = :roomId AND lesson.dayTimestamp = :timestamp")
    abstract fun getLessonsByRoom(roomId: Long, timestamp: Long): Flow<List<Lesson>>

    @Query("DELETE FROM lesson WHERE classId = :classId AND dayTimestamp = :timestamp")
    abstract suspend fun deleteLessonByClassIdAndTimestamp(classId: Long, timestamp: Long)

    @Insert
    abstract suspend fun insert(lesson: Lesson): Long

    @Query("DELETE FROM lesson")
    abstract suspend fun deleteAll()
}
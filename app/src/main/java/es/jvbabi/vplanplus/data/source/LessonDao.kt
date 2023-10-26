package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Lesson

@Dao
abstract class LessonDao {
    @Query("SELECT * FROM lesson WHERE classId = :classId AND timestamp = :timestamp")
    abstract suspend fun getLessonsByClass(classId: Int, timestamp: Long): List<Lesson>

    @Query("SELECT * FROM lesson LEFT JOIN default_lesson ON lesson.defaultLessonId = default_lesson.id WHERE ((default_lesson.teacherId = :teacherId AND lesson.changedTeacherId IS NULL) OR (lesson.changedTeacherId = :teacherId)) AND lesson.timestamp = :timestamp")
    abstract suspend fun getLessonsByTeacher(teacherId: Int, timestamp: Long): List<Lesson>

    @Query("SELECT * FROM lesson WHERE roomId = :roomId AND timestamp = :timestamp")
    abstract suspend fun getLessonsByRoom(roomId: Int, timestamp: Long): List<Lesson>

    @Query("DELETE FROM lesson WHERE classId = :classId AND timestamp = :timestamp")
    abstract suspend fun deleteLessonByClassIdAndTimestamp(classId: Int, timestamp: Long)

    @Insert
    abstract suspend fun insert(lesson: Lesson)
}
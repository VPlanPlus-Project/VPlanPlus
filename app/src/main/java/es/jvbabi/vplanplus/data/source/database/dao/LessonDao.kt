package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Lesson

@Dao
abstract class LessonDao {
    @Query("SELECT * FROM lesson WHERE classId = :classId AND dayTimestamp = :timestamp")
    abstract suspend fun getLessonsByClass(classId: Long, timestamp: Long): List<Lesson>

    @Query("SELECT * FROM lesson WHERE ((changedTeacherId IS NULL AND originalTeacherId = :teacherId) OR changedTeacherId = :teacherId) AND dayTimestamp = :timestamp")
    abstract suspend fun getLessonsByTeacher(teacherId: Long, timestamp: Long): List<Lesson>

    @Query("SELECT * FROM lesson WHERE dayTimestamp = :timestamp")
    abstract suspend fun getAllLessons(timestamp: Long): List<Lesson>

    @Query("DELETE FROM lesson WHERE classId = :classId AND dayTimestamp = :timestamp")
    abstract suspend fun deleteLessonByClassIdAndTimestamp(classId: Long, timestamp: Long)

    @Insert
    abstract suspend fun insert(lesson: Lesson): Long
}
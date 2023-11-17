package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.combined.CLesson
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
abstract class LessonDao {

    @Transaction
    @Query("SELECT * FROM lesson WHERE classLessonRefId = :classId AND day = :timestamp AND version = :version")
    abstract fun getLessonsByClass(classId: Long, timestamp: LocalDate, version: Long): Flow<List<CLesson>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM lesson LEFT JOIN lesson_teacher_crossover ON lesson.lessonId = lesson_teacher_crossover.ltcLessonId WHERE lesson_teacher_crossover.ltcTeacherId = :teacherId AND lesson.day = :timestamp AND version = :version")
    abstract fun getLessonsByTeacher(teacherId: Long, timestamp: LocalDate, version: Long): Flow<List<CLesson>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM lesson LEFT JOIN lesson_room_crossover ON lesson.lessonId = lesson_room_crossover.lrcLessonId AND lesson_room_crossover.lrcRoomId = :roomId AND day = :timestamp AND version = :version")
    abstract fun getLessonsByRoom(roomId: Long, timestamp: LocalDate, version: Long): Flow<List<CLesson>>

    @Insert
    abstract suspend fun insertLesson(lesson: DbLesson): Long

    @Query("DELETE FROM lesson")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM lesson WHERE classLessonRefId = :classId AND day = :timestamp")
    abstract suspend fun deleteLessonsByClassAndDate(classId: Long, timestamp: LocalDate)
}
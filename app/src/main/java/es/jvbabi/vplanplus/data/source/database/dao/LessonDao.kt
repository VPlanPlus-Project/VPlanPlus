package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.RoomWarnings
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.combined.CLesson
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class LessonDao {

    @Transaction
    @Query("SELECT * FROM lesson WHERE group_id = :classId AND day = :timestamp AND version = :version ORDER by lesson_number ASC")
    abstract fun getLessonsByClass(classId: Int, timestamp: Long, version: Long): Flow<List<CLesson>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM lesson LEFT JOIN default_lesson ON default_lesson.id = lesson.default_lesson_id WHERE day = :timestamp AND version = :version AND (lesson.id IN (SELECT school_entity_id FROM lesson_teacher_crossover WHERE school_entity_id = :teacherId) OR default_lesson.teacher_id = :teacherId) ORDER by lesson_number ASC")
    @Suppress(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getLessonsByTeacher(teacherId: UUID, timestamp: Long, version: Long): Flow<List<CLesson>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM lesson WHERE day = :timestamp AND version = :version AND id IN (SELECT lesson_id FROM lesson_room_crossover WHERE room_id = :roomId) ORDER by lesson_number ASC")
    abstract fun getLessonsByRoom(roomId: Int, timestamp: Long, version: Long): Flow<List<CLesson>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Suppress(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM lesson LEFT JOIN school_entity ON school_entity.id = lesson.group_id WHERE school_entity.school_id = :schoolId AND day = :timestamp AND version = :version")
    abstract fun getLessonsForSchool(schoolId: Int, timestamp: Long, version: Long): Flow<List<CLesson>>

    @Upsert
    abstract suspend fun insertLesson(lesson: DbLesson): Long

    @Query("DELETE FROM lesson")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM lesson WHERE group_id = :classId AND day = :timestamp AND version = :version")
    abstract suspend fun deleteLessonsByGroupAndDate(classId: Int, timestamp: Long, version: Long)

    @Query("DELETE FROM lesson WHERE version = :version")
    abstract suspend fun deleteLessonsByVersion(version: Long)

    @Transaction
    open suspend fun insertLessons(lessons: List<DbLesson>) {
        lessons.forEach { lesson ->
            insertLesson(lesson)
        }
    }
}
package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.RoomWarnings
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.combined.CLesson
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class LessonDao {

    @Transaction
    @Query("SELECT * FROM lesson WHERE classLessonRefId = :classId AND day = :timestamp AND version = :version ORDER by lessonNumber ASC")
    abstract fun getLessonsByClass(classId: UUID, timestamp: Long, version: Long): Flow<List<CLesson>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM lesson LEFT JOIN default_lesson ON default_lesson.defaultLessonId = lesson.defaultLessonId WHERE day = :timestamp AND version = :version AND (lessonId IN (SELECT lsecSchoolEntityId FROM lesson_se_crossover WHERE lsecSchoolEntityId = :teacherId) OR default_lesson.teacherId = :teacherId) ORDER by lessonNumber ASC")
    @Suppress(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getLessonsByTeacher(teacherId: UUID, timestamp: Long, version: Long): Flow<List<CLesson>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM lesson WHERE day = :timestamp AND version = :version AND lessonId IN (SELECT lsecLessonId FROM lesson_se_crossover WHERE lsecSchoolEntityId = :roomId) ORDER by lessonNumber ASC")
    abstract fun getLessonsByRoom(roomId: UUID, timestamp: Long, version: Long): Flow<List<CLesson>>

    @Insert
    abstract suspend fun insertLesson(lesson: DbLesson): Long

    @Query("DELETE FROM lesson")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM lesson WHERE classLessonRefId = :classId AND day = :timestamp AND version = :version")
    abstract suspend fun deleteLessonsByClassAndDate(classId: UUID, timestamp: Long, version: Long)

    @Query("DELETE FROM lesson WHERE version = :version")
    abstract suspend fun deleteLessonsByVersion(version: Long)

    @Transaction
    open suspend fun insertLessons(lessons: List<DbLesson>) {
        lessons.forEach { lesson ->
            insertLesson(lesson)
        }
    }
}
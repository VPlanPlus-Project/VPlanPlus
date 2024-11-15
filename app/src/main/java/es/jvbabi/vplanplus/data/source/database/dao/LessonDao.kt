package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.combined.CLesson
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LessonDao {

    @Transaction
    @Query("SELECT * FROM lesson WHERE version = :version AND day = :timestamp")
    abstract fun getLessons(timestamp: Long, version: Long): Flow<List<CLesson>>

    @Transaction
    @Query("SELECT * FROM lesson WHERE version = :version AND day = :timestamp AND group_id = :groupId")
    abstract fun getLessonsByGroup(timestamp: Long, version: Long, groupId: Int): Flow<List<CLesson>>

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
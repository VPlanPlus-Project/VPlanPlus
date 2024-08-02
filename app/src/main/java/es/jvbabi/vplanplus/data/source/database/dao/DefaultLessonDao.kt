package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.combined.CDefaultLesson
import java.util.UUID

@Dao
abstract class DefaultLessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(defaultLesson: DbDefaultLesson)

    @Query("SELECT * FROM default_lesson WHERE class_id = :groupId")
    @Transaction
    abstract suspend fun getDefaultLessonByGroupId(groupId: Int): List<CDefaultLesson>

    @Query("UPDATE default_lesson SET teacher_id = :teacherId WHERE class_id = :groupId AND vp_id = :vpId")
    abstract suspend fun updateTeacherId(groupId: Int, vpId: Int, teacherId: UUID)

    @Query("DELETE FROM default_lesson WHERE id = :id")
    abstract suspend fun deleteById(id: UUID)

    @Query("SELECT * FROM default_lesson")
    @Transaction
    abstract suspend fun getDefaultLessons(): List<CDefaultLesson>
}
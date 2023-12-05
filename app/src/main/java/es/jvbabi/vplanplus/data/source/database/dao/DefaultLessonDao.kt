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

    @Query("SELECT * FROM default_lesson WHERE vpId = :id")
    @Transaction
    abstract suspend fun getDefaultLessonByVpId(id: Long): CDefaultLesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(defaultLesson: DbDefaultLesson)
    @Query("SELECT * FROM default_lesson WHERE classId = :classId")
    @Transaction
    abstract suspend fun getDefaultLessonByClassId(classId: UUID): List<CDefaultLesson>
}
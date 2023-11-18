package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.DefaultLesson

@Dao
abstract class DefaultLessonDao {

    @Query("SELECT * FROM default_lesson WHERE vpId = :id")
    abstract suspend fun getDefaultLessonByVpId(id: Long): DefaultLesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(defaultLesson: DefaultLesson)

    @Query("SELECT * FROM default_lesson WHERE defaultLessonId = :id")
    abstract suspend fun getDefaultLessonById(id: Long): DefaultLesson?
}
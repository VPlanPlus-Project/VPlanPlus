package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.DefaultLesson

@Dao
abstract class DefaultLessonDao {

    @Query("SELECT * FROM default_lesson WHERE vpId = :id")
    abstract suspend fun getDefaultLessonByVpId(id: Long): DefaultLesson?

    @Query("INSERT INTO default_lesson (vpId, subject, teacherId, classId) VALUES (:vpId, :subject, :teacherId, :classId)")
    abstract suspend fun insert(vpId: Long, subject: String, teacherId: Long?, classId: Long): Long

    @Query("SELECT * FROM default_lesson WHERE id = :id")
    abstract suspend fun getDefaultLessonById(id: Long): DefaultLesson?
}
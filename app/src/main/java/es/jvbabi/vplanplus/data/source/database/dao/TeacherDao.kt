package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Teacher

@Dao
abstract class TeacherDao {
    @Insert
    abstract suspend fun insertTeacher(teacher: Teacher): Long

    @Query("SELECT * FROM teacher WHERE schoolTeacherRefId = :schoolId ORDER BY acronym")
    abstract suspend fun getTeachersBySchoolId(schoolId: Long): List<Teacher>

    @Query("SELECT * FROM teacher WHERE schoolTeacherRefId = :schoolId AND acronym = :acronym")
    abstract suspend fun find(schoolId: Long, acronym: String): Teacher?

    @Query("SELECT * FROM teacher WHERE teacherId = :id")
    abstract fun getTeacherById(id: Long): Teacher?

    @Query("DELETE FROM teacher WHERE schoolTeacherRefId = :schoolId")
    abstract suspend fun deleteTeachersBySchoolId(schoolId: Long)
}
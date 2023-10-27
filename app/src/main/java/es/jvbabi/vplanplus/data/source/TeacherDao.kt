package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Teacher

@Dao
abstract class TeacherDao {
    @Insert
    abstract suspend fun insertTeacher(teacher: Teacher): Long

    @Query("SELECT * FROM teacher WHERE schoolId = :schoolId")
    abstract suspend fun getTeachersBySchoolId(schoolId: Long): List<Teacher>

    @Query("SELECT * FROM teacher WHERE schoolId = :schoolId AND acronym = :acronym")
    abstract suspend fun find(schoolId: Long, acronym: String): Teacher?

    @Query("SELECT * FROM teacher WHERE id = :id")
    abstract suspend fun getTeacherById(id: Long): Teacher?
}
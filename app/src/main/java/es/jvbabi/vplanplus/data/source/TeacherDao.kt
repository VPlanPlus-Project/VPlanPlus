package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Teacher

@Dao
abstract class TeacherDao {
    @Insert
    abstract suspend fun insertTeacher(teacher: Teacher)

    @Query("SELECT * FROM teacher WHERE schoolId = :schoolId")
    abstract suspend fun getTeachersBySchoolId(schoolId: String): List<Teacher>

    @Query("SELECT * FROM teacher WHERE schoolId = :schoolId AND acronym = :acronym")
    abstract suspend fun find(schoolId: String, acronym: String): Teacher?

    @Query("SELECT * FROM teacher WHERE id = :id")
    abstract suspend fun getTeacherById(id: Int): Teacher?
}
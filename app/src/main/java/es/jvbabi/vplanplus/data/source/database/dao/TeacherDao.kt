package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbTeacher
import es.jvbabi.vplanplus.data.model.combined.CTeacher
import java.util.UUID

@Dao
abstract class TeacherDao {
    @Insert
    abstract suspend fun insertTeacher(teacher: DbTeacher): Long

    @Transaction
    @Query("SELECT * FROM teacher WHERE schoolTeacherRefId = :schoolId ORDER BY acronym")
    abstract suspend fun getTeachersBySchoolId(schoolId: Long): List<CTeacher>

    @Transaction
    @Query("SELECT * FROM teacher WHERE schoolTeacherRefId = :schoolId AND acronym = :acronym")
    abstract suspend fun find(schoolId: Long, acronym: String): CTeacher?

    @Transaction
    @Query("SELECT * FROM teacher WHERE teacherId = :id")
    abstract fun getTeacherById(id: UUID): CTeacher?

    @Query("DELETE FROM teacher WHERE schoolTeacherRefId = :schoolId")
    abstract suspend fun deleteTeachersBySchoolId(schoolId: Long)
}
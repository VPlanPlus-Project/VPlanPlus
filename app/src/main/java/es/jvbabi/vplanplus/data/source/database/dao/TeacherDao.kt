package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import java.util.UUID

@Dao
abstract class TeacherDao {
    @Insert
    abstract suspend fun insertTeacher(teacher: DbSchoolEntity): Long

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId ORDER BY name AND type = :type")
    abstract suspend fun getTeachersBySchoolId(schoolId: Long, type: SchoolEntityType = SchoolEntityType.TEACHER): List<DbSchoolEntity>

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId AND name = :acronym AND type = :type")
    abstract suspend fun find(schoolId: Long, acronym: String, type: SchoolEntityType = SchoolEntityType.TEACHER): DbSchoolEntity?

    @Transaction
    @Query("SELECT * FROM school_entity WHERE id = :id AND type = :type")
    abstract fun getTeacherById(id: UUID, type: SchoolEntityType = SchoolEntityType.TEACHER): DbSchoolEntity?

    @Query("DELETE FROM school_entity WHERE schoolId = :schoolId")
    abstract suspend fun deleteTeachersBySchoolId(schoolId: Long)
}
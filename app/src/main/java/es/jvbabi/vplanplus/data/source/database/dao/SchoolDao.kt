package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.School

@Dao
abstract class SchoolDao {

    @Query("SELECT * FROM school")
    abstract suspend fun getAll(): List<School>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(school: School)

    @Query("UPDATE school SET name = :name WHERE schoolId = :schoolId")
    abstract suspend fun updateName(schoolId: Long, name: String)

    @Query("SELECT * FROM school WHERE schoolId = :schoolId")
    abstract fun getSchoolFromId(schoolId: Long): School

    @Query("SELECT * FROM school WHERE name = :schoolName")
    abstract suspend fun getSchoolByName(schoolName: String): School

    @Query("DELETE FROM school WHERE schoolId = :schoolId")
    abstract suspend fun delete(schoolId: Long)
}
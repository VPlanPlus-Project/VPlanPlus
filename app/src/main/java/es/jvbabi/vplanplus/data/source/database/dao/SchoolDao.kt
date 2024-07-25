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

    @Query("UPDATE school SET name = :name WHERE id = :schoolId")
    abstract suspend fun updateName(schoolId: Int, name: String)

    @Query("SELECT * FROM school WHERE id = :schoolId")
    abstract suspend fun getSchoolFromId(schoolId: Int): School?

    @Query("SELECT * FROM school WHERE sp24_school_id = :sp24SchoolId LIMIT 1")
    abstract suspend fun getSchoolBySp24Id(sp24SchoolId: Int): School?

    @Query("SELECT * FROM school WHERE name = :schoolName")
    abstract suspend fun getSchoolByName(schoolName: String): School

    @Query("DELETE FROM school WHERE id = :schoolId")
    abstract suspend fun delete(schoolId: Int)

    @Query("UPDATE school SET credentials_valid = :credentialsValid WHERE id = :schoolId")
    abstract suspend fun updateCredentialsValid(schoolId: Int, credentialsValid: Boolean?)

    @Query("UPDATE school SET username = :username, password = :password WHERE id = :schoolId")
    abstract suspend fun updateCredentials(schoolId: Int, username: String, password: String)
}
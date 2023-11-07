package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.School
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SchoolDao {

    @Query("SELECT * FROM school")
    abstract fun getAll(): Flow<List<School>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(school: School)

    @Query("UPDATE school SET name = :name WHERE id = :schoolId")
    abstract suspend fun updateName(schoolId: Long, name: String)

    @Query("SELECT * FROM school WHERE id = :schoolId")
    abstract suspend fun getSchoolFromId(schoolId: Long): School
}
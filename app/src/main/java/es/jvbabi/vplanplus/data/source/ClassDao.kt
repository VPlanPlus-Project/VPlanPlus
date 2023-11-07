package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Classes

@Dao
abstract class ClassDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertClass(classObj: Classes): Long

    @Query("SELECT * FROM classes WHERE schoolId = :schoolId AND className = :className")
    abstract suspend fun getClassBySchoolIdAndClassName(schoolId: Long, className: String): Classes?

    @Query("SELECT * FROM classes WHERE id = :id")
    abstract suspend fun getClassById(id: Long): Classes
}
package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbClass
import es.jvbabi.vplanplus.data.model.combined.CClass

@Dao
abstract class ClassDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertClass(classObj: DbClass): Long

    @Transaction
    @Query("SELECT * FROM classes WHERE schoolClassRefId = :schoolId AND className = :className")
    abstract suspend fun getClassBySchoolIdAndClassName(schoolId: Long, className: String): CClass?

    @Transaction
    @Query("SELECT * FROM classes WHERE classId = :id")
    abstract fun getClassById(id: Long): CClass

    @Query("DELETE FROM classes WHERE schoolClassRefId = :schoolId")
    abstract suspend fun deleteClassesBySchoolId(schoolId: Long)

    @Transaction
    @Query("SELECT * FROM classes WHERE schoolClassRefId = :schoolId")
    abstract suspend fun getClassesBySchoolId(schoolId: Long): List<CClass>
}
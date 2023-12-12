package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.data.model.combined.CSchoolEntity
import java.util.UUID

@Dao
abstract class ClassDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertClass(classObj: DbSchoolEntity)

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId AND name = :className AND type = :type")
    abstract suspend fun getClassBySchoolIdAndClassName(schoolId: Long, className: String, type: SchoolEntityType = SchoolEntityType.CLASS): CSchoolEntity?

    @Transaction
    @Query("SELECT * FROM school_entity WHERE id = :id")
    abstract fun getClassById(id: UUID): CSchoolEntity

    @Query("DELETE FROM school_entity WHERE schoolId = :schoolId AND type = :type")
    abstract suspend fun deleteClassesBySchoolId(schoolId: Long, type: SchoolEntityType = SchoolEntityType.CLASS)

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId AND type = :type")
    abstract suspend fun getClassesBySchoolId(schoolId: Long, type: SchoolEntityType = SchoolEntityType.CLASS): List<CSchoolEntity>
}
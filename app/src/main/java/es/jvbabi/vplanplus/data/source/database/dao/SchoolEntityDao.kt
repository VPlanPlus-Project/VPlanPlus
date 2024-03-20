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
abstract class SchoolEntityDao {

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId AND type = :type")
    abstract suspend fun getSchoolEntities(schoolId: Long, type: SchoolEntityType): List<CSchoolEntity>

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId AND name = :name AND type = :type")
    abstract suspend fun getSchoolEntityByName(schoolId: Long, name: String, type: SchoolEntityType): CSchoolEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSchoolEntity(schoolEntity: DbSchoolEntity)

    @Transaction
    open suspend fun insertSchoolEntities(schoolEntities: List<DbSchoolEntity>) {
        schoolEntities.forEach {
            insertSchoolEntity(it)
        }
    }

    @Query("SELECT * FROM school_entity WHERE id = :schoolEntityId")
    @Transaction
    abstract suspend fun getSchoolEntityById(schoolEntityId: UUID): CSchoolEntity?

    @Query("DELETE FROM school_entity WHERE schoolId = :schoolId AND type = :type")
    abstract suspend fun deleteSchoolEntitiesBySchoolId(schoolId: Long, type: SchoolEntityType)

    @Transaction
    @Query("SELECT * FROM school_entity WHERE type = :type")
    abstract suspend fun getSchoolEntitiesByType(type: SchoolEntityType): List<CSchoolEntity>
}
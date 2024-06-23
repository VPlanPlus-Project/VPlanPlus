package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.combined.CGroup
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GroupDao {
    @Upsert
    abstract suspend fun upsert(group: DbGroup)

    @Transaction
    @Query("SELECT * FROM `group` WHERE id = :groupId")
    abstract fun getGroupById(groupId: Int): Flow<CGroup?>

    @Transaction
    @Query("SELECT * FROM `group` WHERE school_id = :schoolId")
    abstract fun getGroupsBySchoolId(schoolId: Int): Flow<List<CGroup>>

    @Query("DELETE FROM `group` WHERE school_id = :schoolId")
    abstract suspend fun deleteGroupsBySchoolId(schoolId: Int)

    @Transaction
    @Query("SELECT * FROM `group`")
    abstract fun getAll(): Flow<List<CGroup>>
}
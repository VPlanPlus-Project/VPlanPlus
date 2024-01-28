package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbVppId
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VppIdDao {
    @Upsert
    abstract suspend fun upsert(vppId: DbVppId)

    @Delete
    abstract suspend fun delete(vppId: DbVppId)

    @Query("SELECT * FROM vpp_id")
    abstract fun getAll(): Flow<List<DbVppId>>
}
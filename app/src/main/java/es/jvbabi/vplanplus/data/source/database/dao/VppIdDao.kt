package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.combined.CVppId
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VppIdDao {
    @Upsert
    abstract suspend fun upsert(vppId: DbVppId)

    @Query("DELETE FROM vpp_id WHERE id = :vppId")
    abstract suspend fun delete(vppId: Int)

    @Query("SELECT * FROM vpp_id")
    abstract fun getAll(): Flow<List<CVppId>>

    @Query("SELECT * FROM vpp_id WHERE id = :vppId")
    abstract suspend fun getVppId(vppId: Int): CVppId?
}
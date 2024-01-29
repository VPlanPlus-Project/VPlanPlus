package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbVppIdToken

@Dao
abstract class VppIdTokenDao {

    @Upsert
    abstract suspend fun insert(token: DbVppIdToken)

    @Query("SELECT * FROM vpp_id_token WHERE vppId = :vppId")
    abstract suspend fun getTokenByVppId(vppId: Int): DbVppIdToken?
}
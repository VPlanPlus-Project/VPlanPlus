package es.jvbabi.vplanplus.data.model

import androidx.room.Entity

@Entity(
    tableName = "vpp_id_cache",
    primaryKeys = ["id"]
)
data class VppIdCache(
    val id: Int,
    val name: String
)

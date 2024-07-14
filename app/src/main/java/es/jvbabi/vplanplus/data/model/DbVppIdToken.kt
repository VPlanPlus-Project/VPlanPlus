package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "vpp_id_token",
    primaryKeys = ["id", "vpp_id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["vpp_id"], unique = true),
        Index(value = ["access_token"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbVppId::class,
            parentColumns = ["id"],
            childColumns = ["vpp_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbVppIdToken(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("vpp_id") val vppId: Int,
    @ColumnInfo("access_token") val accessToken: String,
    @ColumnInfo("schulverwalter_token") val bsToken: String?
)
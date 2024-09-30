package es.jvbabi.vplanplus.data.model.vppid

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "vpp_id_token",
    primaryKeys = ["vpp_id"],
    indices = [
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
    @ColumnInfo("vpp_id") val vppId: Int,
    @ColumnInfo("access_token") val accessToken: String,
    @ColumnInfo("schulverwalter_token") val bsToken: String?
)
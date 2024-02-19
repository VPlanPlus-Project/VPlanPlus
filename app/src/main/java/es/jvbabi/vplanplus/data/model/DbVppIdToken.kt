package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "vpp_id_token",
    primaryKeys = ["id", "vppId"],
    indices = [
        Index(value = ["vppId"], unique = true),
        Index(value = ["token"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbVppId::class,
            parentColumns = ["id"],
            childColumns = ["vppId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbVppIdToken(
    val id: UUID = UUID.randomUUID(),
    val vppId: Int,
    val token: String,
    val bsToken: String?
)
package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.VppId
import java.util.UUID

@Entity(
    tableName = "vpp_id",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class DbVppId(
    val id: Int,
    val name: String,
    val schoolId: Long,
    val className: String,
    val classId: UUID?
) {
    fun toModel(): VppId {
        return VppId(
            id = id,
            name = name,
            schoolId = schoolId,
            school = null,
            className = className,
            classes = null
        )
    }
}
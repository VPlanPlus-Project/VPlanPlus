package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VppId
import java.util.UUID

@Entity(
    tableName = "vpp_id",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbVppId(
    val id: Int,
    val name: String,
    val schoolId: Long,
    val className: String,
    val classId: UUID?,
    val state: State
) {
    fun toModel(): VppId {
        return VppId(
            id = id,
            name = name,
            schoolId = schoolId,
            school = null,
            className = className,
            classes = null,
            state = state
        )
    }
}
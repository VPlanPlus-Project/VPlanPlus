package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.School
import java.util.UUID

@Entity(
    tableName = "classes",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolClassRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["classId"],
    indices = [
        Index(value = ["classId"], unique = true),
        Index(value = ["schoolClassRefId"]),
        Index(value = ["className"])
    ]
)
data class DbClass(
    val classId: UUID = UUID.randomUUID(),
    val schoolClassRefId: Long,
    val className: String,
) {
    override fun toString(): String {
        return "Classes(id=$classId, schoolId='$schoolClassRefId', className='$className')"
    }
}
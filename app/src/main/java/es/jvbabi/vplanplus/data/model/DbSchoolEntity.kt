package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.School
import java.util.UUID

@Entity(
    tableName = "school_entity",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["schoolId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbSchoolEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val schoolId: Long,
    val type: SchoolEntityType
)

enum class SchoolEntityType {
    CLASS,
    TEACHER,
    ROOM
}
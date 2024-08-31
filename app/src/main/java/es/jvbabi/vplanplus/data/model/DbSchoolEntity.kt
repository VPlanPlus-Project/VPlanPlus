package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.DbSchool
import java.util.UUID

@Entity(
    tableName = "school_entity",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["school_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbSchool::class,
            parentColumns = ["id"],
            childColumns = ["school_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbSchoolEntity(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("name") val name: String,
    @ColumnInfo("school_id") val schoolId: Int,
    @ColumnInfo("type") val type: SchoolEntityType
)

enum class SchoolEntityType {
    TEACHER,
    ROOM
}
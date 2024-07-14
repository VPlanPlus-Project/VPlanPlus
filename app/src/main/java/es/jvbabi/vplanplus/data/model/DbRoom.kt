package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.School

@Entity(
    tableName = "room",
    primaryKeys = ["id", "school_id", "name"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["school_id"]),
        Index(value = ["name"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["id"],
            childColumns = ["school_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class DbRoom(
    @ColumnInfo("id") val id: Int,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("school_id") val schoolId: Int,
)
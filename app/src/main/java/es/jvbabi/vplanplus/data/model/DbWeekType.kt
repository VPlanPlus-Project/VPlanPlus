package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import es.jvbabi.vplanplus.domain.model.DbSchool

@Entity(
    tableName = "week_type",
    indices = [
        Index(value = ["id", "name", "school_id"], unique = true),
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
data class DbWeekType(
    @ColumnInfo("name") val name: String,
    @ColumnInfo("school_id") val schoolId: Int,
) {
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
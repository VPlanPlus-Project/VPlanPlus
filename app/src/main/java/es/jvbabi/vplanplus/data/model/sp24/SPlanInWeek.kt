package es.jvbabi.vplanplus.data.model.sp24

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.DbSchool

@Entity(
    tableName = "sp24_splan_in_week",
    primaryKeys = ["school_id", "week_number"],
    indices = [
        Index(value = ["school_id", "week_number"], unique = true)
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
data class SPlanInWeek(
    @ColumnInfo("school_id") val schoolId: Int,
    @ColumnInfo("week_number") val weekNumber: Int,
    @ColumnInfo("has_data") val hasData: Boolean,
)
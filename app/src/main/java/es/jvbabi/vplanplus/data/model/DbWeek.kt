package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "week",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["school_id"]),
        Index(value = ["week_type_id"]),
    ],
)
data class DbWeek(
    @ColumnInfo("school_id") val schoolId: Int,
    @ColumnInfo("week_type_id") val weekTypeId: Int,
    @ColumnInfo("start_date") val startDate: LocalDate,
    @ColumnInfo("end_date") val endDate: LocalDate
) {
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
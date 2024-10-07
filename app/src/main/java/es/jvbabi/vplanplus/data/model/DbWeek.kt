package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import java.time.LocalDate

@Entity(
    tableName = "week",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["school_id"]),
        Index(value = ["week_type_id"]),
    ],
)
data class DbWeek(
    @ColumnInfo("id") val id: Int,
    @ColumnInfo("school_id") val schoolId: Int,
    @ColumnInfo("week_number") val weekNumber: Int,
    @ColumnInfo("week_type_id") val weekTypeId: Int,
    @ColumnInfo("start_date") val startDate: LocalDate,
    @ColumnInfo("end_date") val endDate: LocalDate
)
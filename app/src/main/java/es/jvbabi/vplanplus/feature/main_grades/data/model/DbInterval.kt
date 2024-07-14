package es.jvbabi.vplanplus.feature.main_grades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate

@Entity(
    tableName = "bs_intervals",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["included_interval_id"]),
        Index(value = ["year_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbYear::class,
            parentColumns = ["id"],
            childColumns = ["year_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
    ]
)
data class DbInterval(
    @ColumnInfo("id") val id: Long,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("type") val type: String,
    @ColumnInfo("from") val from: LocalDate,
    @ColumnInfo("to") val to: LocalDate,
    @ColumnInfo("included_interval_id") val includedIntervalId: Long?,
    @ColumnInfo("year_id") val yearId: Long,
)
package es.jvbabi.vplanplus.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate
import java.util.UUID

/**
 * Class that represents a holiday.
 * @param id The id of the holiday (auto-generated)
 * @param schoolId The id of the school the holiday belongs to, null if it is a general holiday
 * @param date The date of the holiday
 */
@Entity(
    tableName = "holiday",
    foreignKeys = [
         ForeignKey(
             entity = DbSchool::class,
             parentColumns = ["id"],
             childColumns = ["school_id"],
             onDelete = ForeignKey.CASCADE
         )
    ],
    primaryKeys = ["id"],
    indices = [
        Index("id", unique = true),
        Index("school_id")
    ]
)
data class Holiday(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("school_id") val schoolId: Long?,
    @ColumnInfo("date") val date: LocalDate,
) {
    override fun toString(): String {
        return "Holiday(id=$id, schoolId=$schoolId, date=$date)"
    }
}
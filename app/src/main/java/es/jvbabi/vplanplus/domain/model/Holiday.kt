package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate
import java.util.UUID

/**
 * Class that represents a holiday.
 * @param holidayId The id of the holiday (auto-generated)
 * @param schoolHolidayRefId The id of the school the holiday belongs to, null if it is a general holiday
 * @param date The date of the holiday
 */
@Entity(
    tableName = "holiday",
    foreignKeys = [
         ForeignKey(
             entity = School::class,
             parentColumns = ["schoolId"],
             childColumns = ["schoolHolidayRefId"],
             onDelete = ForeignKey.CASCADE
         )
    ],
    primaryKeys = ["holidayId"],
    indices = [
        Index("holidayId", unique = true),
        Index("schoolHolidayRefId")
    ]
)
data class Holiday(
    val holidayId: UUID = UUID.randomUUID(),
    val schoolHolidayRefId: Long?,
    val date: LocalDate,
) {
    override fun toString(): String {
        return "Holiday(id=$holidayId, schoolId=$schoolHolidayRefId, date=$date)"
    }
}
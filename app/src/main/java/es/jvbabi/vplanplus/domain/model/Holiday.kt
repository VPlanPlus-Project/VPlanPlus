package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

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
             entity = School::class,
             parentColumns = ["id"],
             childColumns = ["schoolId"],
             onDelete = ForeignKey.CASCADE
         )
    ]
)
data class Holiday(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val schoolId: Long?,
    val date: LocalDate,
) {
    override fun toString(): String {
        return "Holiday(id=$id, schoolId=$schoolId, date=$date)"
    }
}
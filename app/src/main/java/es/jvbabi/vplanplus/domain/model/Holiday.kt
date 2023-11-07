package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Class that represents a holiday.
 * @param id The id of the holiday (auto-generated)
 * @param schoolId The id of the school the holiday belongs to, null if it is a general holiday
 * @param timestamp The timestamp of the holiday at 00:00:00 (UTC)
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
    val timestamp: Long,
) {
    override fun toString(): String {
        return "Holiday(id=$id, schoolId=$schoolId, timestamp=$timestamp)"
    }
}
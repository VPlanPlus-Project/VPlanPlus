package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Class that represents a holiday.
 * @param id The id of the holiday (auto-generated)
 * @param schoolId The id of the school the holiday belongs to, null if it is a general holiday
 * @param timestamp The timestamp of the holiday at 00:00:00 (UTC)
 */
@Entity(
    tableName = "holiday",
    indices = [
        Index(value = ["id",], unique = true)
    ]
)
data class Holiday(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val schoolId: String?,
    val timestamp: Long,
)

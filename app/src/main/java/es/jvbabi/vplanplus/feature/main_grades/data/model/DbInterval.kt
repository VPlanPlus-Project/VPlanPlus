package es.jvbabi.vplanplus.feature.main_grades.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate

@Entity(
    tableName = "bs_intervals",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = DbYear::class,
            parentColumns = ["id"],
            childColumns = ["yearId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class DbInterval(
    val id: Long,
    val name: String,
    val type: String,
    val from: LocalDate,
    val to: LocalDate,
    val includedIntervalId: Long?,
    val yearId: Long,
)
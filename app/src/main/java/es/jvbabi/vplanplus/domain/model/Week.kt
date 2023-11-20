package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "weeks",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolWeekRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Week(
    @PrimaryKey(autoGenerate = true) val weekId: Long = 0,
    val schoolWeekRefId: Long,
    val week: Int,
    val start: LocalDate,
    val end: LocalDate,
    val type: String
)

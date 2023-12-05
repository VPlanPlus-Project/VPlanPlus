package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "weeks",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolWeekRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["weekId"],
    indices = [
        Index("weekId", unique = true),
        Index("schoolWeekRefId")
    ]
)
data class Week(
    val weekId: UUID = UUID.randomUUID(),
    val schoolWeekRefId: Long,
    val week: Int,
    val start: LocalDate,
    val end: LocalDate,
    val type: String
)

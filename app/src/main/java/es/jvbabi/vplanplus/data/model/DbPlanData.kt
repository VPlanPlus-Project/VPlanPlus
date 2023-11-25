package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import es.jvbabi.vplanplus.domain.model.DayDataType
import es.jvbabi.vplanplus.domain.model.School
import java.time.LocalDate
import java.util.UUID

/**
 * Represents plan metadata
 * @param id Unique identifier
 * @param date Date of the plan
 * @param schoolId School identifier
 * @param planDate Date where plan was created
 */
@Entity(
    tableName = "plan_data",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbPlanData(
    val id: UUID = UUID.randomUUID(),
    val date: LocalDate,
    val schoolId: Long,
    val planDate: LocalDate,
    val dayDataType: DayDataType,
)

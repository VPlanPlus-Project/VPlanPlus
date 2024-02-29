package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.School
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
    tableName = "plan_data",
    primaryKeys = ["id", "schoolId", "planDate"],
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id", unique = true),
        Index("schoolId")
    ]
)
data class DbPlanData(
    val id: UUID = UUID.randomUUID(),
    val createDate: ZonedDateTime,
    val schoolId: Long,
    val planDate: ZonedDateTime,
    val info: String?,
    val version: Long,
)

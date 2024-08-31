package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.DbSchool
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
    tableName = "plan_data",
    primaryKeys = ["id", "school_id", "plan_date"],
    foreignKeys = [
        ForeignKey(
            entity = DbSchool::class,
            parentColumns = ["id"],
            childColumns = ["school_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id", unique = true),
        Index("school_id")
    ]
)
data class DbPlanData(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("create_date") val createDate: ZonedDateTime,
    @ColumnInfo("school_id") val schoolId: Int,
    @ColumnInfo("plan_date") val planDate: ZonedDateTime,
    @ColumnInfo("info") val info: String?,
    @ColumnInfo("version") val version: Long,
)

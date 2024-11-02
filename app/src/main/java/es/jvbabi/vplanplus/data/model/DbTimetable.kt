package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "timetable",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["class_id", "week_id", "week_type_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["class_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbWeek::class,
            parentColumns = ["id"],
            childColumns = ["week_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbWeekType::class,
            parentColumns = ["id"],
            childColumns = ["week_type_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbTimetable(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("class_id") val classId: Int,
    @ColumnInfo("day_of_week") val dayOfWeek: Int,
    @ColumnInfo("week_id") val weekId: Int?,
    @ColumnInfo("week_type_id") val weekTypeId: Int?,
    @ColumnInfo("lesson_number") val lessonNumber: Int,
    @ColumnInfo("subject") val subject: String,
)
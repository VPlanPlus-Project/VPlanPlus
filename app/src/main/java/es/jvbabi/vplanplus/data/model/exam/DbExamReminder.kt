package es.jvbabi.vplanplus.data.model.exam

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import java.util.UUID

@Entity(
    tableName = "exam_reminders",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["exam_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbExam::class,
            parentColumns = ["id"],
            childColumns = ["exam_id"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = DbClassProfile::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = CASCADE
        )
    ]
)
data class DbExamReminder(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "days_before") val daysBefore: Int,
    @ColumnInfo(name = "exam_id") val examId: Int,
    @ColumnInfo(name = "profile_id") val profileId: UUID,
)
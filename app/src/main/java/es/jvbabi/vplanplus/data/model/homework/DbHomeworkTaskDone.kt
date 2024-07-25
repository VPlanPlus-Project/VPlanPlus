package es.jvbabi.vplanplus.data.model.homework

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import java.util.UUID

@Entity(
    tableName = "homework_task_done",
    primaryKeys = ["task_id", "profile_id"],
    indices = [
        Index(value = ["task_id"]),
        Index(value = ["profile_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbHomeworkTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbClassProfile::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbHomeworkTaskDone(
    @ColumnInfo("task_id") val taskId: Int,
    @ColumnInfo("profile_id") val profileId: UUID,
    @ColumnInfo("is_done") val isDone: Boolean,
)
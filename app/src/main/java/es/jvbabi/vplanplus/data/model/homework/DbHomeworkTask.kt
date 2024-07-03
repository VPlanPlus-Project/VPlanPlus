package es.jvbabi.vplanplus.data.model.homework

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask

@Entity(
    tableName = "homework_task",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["homework_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbHomework::class,
            parentColumns = ["id"],
            childColumns = ["homework_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbHomeworkTask(
    @ColumnInfo("id") val id: Int,
    @ColumnInfo("homework_id") val homeworkId: Int,
    @ColumnInfo("content") val content: String,
    @ColumnInfo("is_done") val isDone: Boolean,
) {
    fun toModel(): HomeworkTask {
        return HomeworkTask(
            id = id,
            content = content,
            isDone = isDone,
            homeworkId = homeworkId
        )
    }
}
package es.jvbabi.vplanplus.data.model.homework

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.UUID

@Entity(
    tableName = "homework_document",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = DbHomework::class,
            parentColumns = ["id"],
            childColumns = ["homework_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DbHomeworkDocument(
    val id: Int,
    @ColumnInfo(name = "homework_id") val homeworkId: Long?
)
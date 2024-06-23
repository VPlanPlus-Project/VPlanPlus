package es.jvbabi.vplanplus.data.model.homework

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "homework_document",
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
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DbHomeworkDocument(
    val id: Int,
    @ColumnInfo("file_name") val fileName: String,
    @ColumnInfo("file_type") val fileType: String,
    @ColumnInfo("homework_id") val homeworkId: Long?
)
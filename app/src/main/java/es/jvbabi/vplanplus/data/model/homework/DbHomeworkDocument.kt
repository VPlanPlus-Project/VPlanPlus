package es.jvbabi.vplanplus.data.model.homework

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType

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
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbHomeworkDocument(
    val id: Int,
    @ColumnInfo("file_name") val fileName: String,
    @ColumnInfo("file_type") val fileType: String,
    @ColumnInfo("homework_id") val homeworkId: Long,
    @ColumnInfo("is_downloaded") val isDownloaded: Boolean,
    @ColumnInfo("size") val size: Long
) {
    fun toModel(): HomeworkDocument {
        return HomeworkDocument(
            documentId = id,
            homeworkId = homeworkId.toInt(),
            type = HomeworkDocumentType.fromExtension(fileType),
            name = fileName,
            isDownloaded = isDownloaded,
            size = size
        )
    }
}
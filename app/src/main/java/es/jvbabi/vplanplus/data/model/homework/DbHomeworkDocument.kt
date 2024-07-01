package es.jvbabi.vplanplus.data.model.homework

import android.content.Context
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import java.io.File

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
    @ColumnInfo("homework_id") val homeworkId: Long
) {
    fun toModel(context: Context): HomeworkDocument {
        return HomeworkDocument(
            documentId = id,
            homeworkId = homeworkId.toInt(),
            uri = Uri.fromFile(File(context.filesDir, "homework_documents/${id}")),
            type = HomeworkDocumentType.fromExtension(fileType),
            name = fileName
        )
    }
}
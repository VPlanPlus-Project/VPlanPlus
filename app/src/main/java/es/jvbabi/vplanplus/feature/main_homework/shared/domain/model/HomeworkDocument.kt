package es.jvbabi.vplanplus.feature.main_homework.shared.domain.model

import android.net.Uri

data class HomeworkDocument(
    val documentId: Int,
    val homeworkId: Int,
    val uri: Uri,
    val type: HomeworkDocumentType,
    val name: String?
)

enum class HomeworkDocumentType(val extension: String) {
    JPG("jpg"),
    PDF("pdf");

    companion object {
        fun fromExtension(extension: String): HomeworkDocumentType {
            return when (extension) {
                "jpg" -> JPG
                "pdf" -> PDF
                else -> throw IllegalArgumentException("Unknown extension: $extension")
            }
        }
    }
}
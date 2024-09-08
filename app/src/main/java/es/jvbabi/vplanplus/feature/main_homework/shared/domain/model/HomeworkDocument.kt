package es.jvbabi.vplanplus.feature.main_homework.shared.domain.model

sealed class HomeworkDocument(
    val documentId: Int,
    val homeworkId: Int,
    val type: HomeworkDocumentType,
    val name: String?,
    val size: Long
) {
    class SavedHomeworkDocument(
        documentId: Int,
        homeworkId: Int,
        type: HomeworkDocumentType,
        name: String?,
        size: Long
    ) : HomeworkDocument(documentId, homeworkId, type, name, size)

    class OnlineHomeworkDocument(
        documentId: Int,
        homeworkId: Int,
        type: HomeworkDocumentType,
        name: String?,
        size: Long
    ) : HomeworkDocument(documentId, homeworkId, type, name, size)
}


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
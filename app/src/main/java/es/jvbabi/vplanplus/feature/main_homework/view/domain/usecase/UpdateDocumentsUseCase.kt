package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import android.net.Uri
import es.jvbabi.vplanplus.data.repository.FileRepository
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class UpdateDocumentsUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val fileRepository: FileRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(
        homework: Homework,
        newDocuments: List<DocumentUpdate.NewDocument>,
        editedDocuments: List<DocumentUpdate.EditedDocument>,
        documentsToDelete: List<HomeworkDocument>
    ) {
        val vppId = (getCurrentProfileUseCase().first() as? ClassProfile ?: return).vppId
        if (homework.id > 0 && vppId == null) throw IllegalStateException("Profile must be a class profile to edit homework")
        newDocuments.forEach { newDocument ->
            val content = fileRepository.readBytes(newDocument.uri) ?: return@forEach
            val id = homeworkRepository.addDocumentToHomework(vppId, homework, content, newDocument.name, HomeworkDocumentType.fromExtension(newDocument.extension)).value ?: return@forEach
            fileRepository.writeBytes("homework_documents", id.toString(), content)
        }
        editedDocuments.forEach { editedDocument ->
            val document = homeworkRepository.getDocumentById(editedDocument.uri.lastPathSegment.toString().toInt()) ?: return@forEach
            homeworkRepository.editDocument(vppId, document, editedDocument.name)
        }
        documentsToDelete.forEach { document ->
            if (homeworkRepository.deleteDocument(vppId, document) == HomeworkModificationResult.FAILED) return@forEach
            fileRepository.deleteFile("homework_documents", document.documentId.toString())
        }
    }
}

sealed class DocumentUpdate(
    val uri: Uri,
    val name: String = UUID.randomUUID().toString()
) {
    class NewDocument(
        uri: Uri,
        name: String = UUID.randomUUID().toString(),
        val extension: String
    ) : DocumentUpdate(uri, name)

    class EditedDocument(
        uri: Uri,
        name: String = UUID.randomUUID().toString()
    ) : DocumentUpdate(uri, name)
}
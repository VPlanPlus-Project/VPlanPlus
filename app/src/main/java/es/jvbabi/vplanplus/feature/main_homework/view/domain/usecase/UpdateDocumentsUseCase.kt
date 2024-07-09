package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import android.net.Uri
import androidx.core.net.toFile
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
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
        newDocuments: Collection<DocumentUpdate.NewDocument>,
        editedDocuments: Collection<DocumentUpdate.EditedDocument>,
        documentsToDelete: Collection<HomeworkDocument>,
        onUploading: (uri: Uri, uploadProgress: Float) -> Unit
    ) {
        val vppId = (getCurrentProfileUseCase().first() as? ClassProfile ?: return).vppId
        if (homework.id > 0 && vppId == null) throw IllegalStateException("Profile must be a class profile to edit homework")
        newDocuments.forEach { newDocument ->
            val content = fileRepository.readBytes(newDocument.uri.toFile()) ?: return@forEach
            var documentId: Int? = null
            if (homework.id > 0 && vppId != null) {
                documentId = homeworkRepository.addDocumentCloud(
                    vppId = vppId,
                    name = newDocument.name,
                    type = HomeworkDocumentType.fromExtension(newDocument.extension),
                    homeworkId = homework.id.toInt(),
                    content = content,
                    onUploading = { sent, of ->
                        onUploading(newDocument.uri, (sent.toFloat() / of))
                    }
                ).value ?: return@forEach
            }
            documentId = homeworkRepository.addDocumentDb(
                documentId = documentId,
                homeworkId = homework.id.toInt(),
                name = newDocument.name,
                type = HomeworkDocumentType.fromExtension(newDocument.extension),
            )
            fileRepository.writeBytes("homework_documents", "$documentId.${newDocument.extension}", content)
        }
        documentsToDelete.forEach { document ->
            if (homework is Homework.CloudHomework && vppId != null) {
                homeworkRepository.deleteDocumentCloud(vppId, document).value ?: return@forEach
            }
            homeworkRepository.deleteDocumentDb(document)
            fileRepository.deleteFile("homework_documents", "${document.documentId}.${document.type.extension}")
        }
        editedDocuments.forEach { editedDocument ->
            val document = homeworkRepository.getDocumentById(editedDocument.uri.lastPathSegment.toString().toInt()) ?: return@forEach
            if (homework is Homework.CloudHomework && vppId != null) {
                homeworkRepository.changeDocumentNameCloud(vppId, document, editedDocument.name).value ?: return@forEach
            }
            homeworkRepository.changeDocumentNameDb(document, editedDocument.name)
        }
    }
}

sealed class DocumentUpdate(
    val uri: Uri,
    val name: String = UUID.randomUUID().toString()
) {
    class NewDocument(
        uri: Uri,
        name: String = uri.toFile().name,
        val extension: String
    ) : DocumentUpdate(uri, name)

    class EditedDocument(
        uri: Uri,
        name: String = UUID.randomUUID().toString(),
        val documentId: Int
    ) : DocumentUpdate(uri, name)
}
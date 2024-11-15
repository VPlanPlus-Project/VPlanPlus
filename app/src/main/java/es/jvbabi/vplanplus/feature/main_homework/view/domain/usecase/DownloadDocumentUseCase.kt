package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class DownloadDocumentUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val fileRepository: FileRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(homework: PersonalizedHomework, document: HomeworkDocument, onDownloading: (sent: Long, total: Long) -> Unit) {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return
        val vppId = profile.vppId
        val group = profile.group
        val data = homeworkRepository.downloadHomeworkDocument(vppId, group, homework.homework.id, document.documentId, onDownloading) ?: return
        fileRepository.writeBytes("homework_documents", "${document.documentId}.${document.type.extension}", data)
        homeworkRepository.updateHomeworkDocumentsFileState()
    }
}
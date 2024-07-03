package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class DeleteHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val fileRepository: FileRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
){

    suspend operator fun invoke(homework: Homework): HomeworkModificationResult {
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return HomeworkModificationResult.FAILED
        homework.documents.forEach { document ->
            fileRepository.deleteFile("homework_documents", document.documentId.toString())
        }
        if (homework is CloudHomework && profile.vppId != null) {
            homeworkRepository.deleteHomeworkFromCloud(profile.vppId, homework).value ?: return HomeworkModificationResult.FAILED
        }
        homeworkRepository.deleteHomeworkFromDb(homework)
        return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
    }
}
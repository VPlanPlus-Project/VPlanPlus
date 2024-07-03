package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class ChangeVisibilityUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {

    suspend operator fun invoke(homework: CloudHomework): HomeworkModificationResult {
        val vppId = (getCurrentProfileUseCase().first() as? ClassProfile)?.vppId ?: return HomeworkModificationResult.FAILED
        homeworkRepository.changeHomeworkSharingCloud(vppId, homework, !homework.isPublic).value ?: return HomeworkModificationResult.FAILED
        homeworkRepository.changeHomeworkSharingDb(homework, !homework.isPublic)
        return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
    }
}
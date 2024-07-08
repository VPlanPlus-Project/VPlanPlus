package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class UpdateHomeworkVisibilityUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(homework: CloudHomework, isPublicOrVisible: Boolean): Boolean {
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return false
        val isOwner = homework.createdBy.id == profile.vppId?.id
        if (isOwner && profile.vppId != null) {
            homeworkRepository.changeHomeworkSharingCloud(profile.vppId, homework, isPublicOrVisible).value ?: return false
            homeworkRepository.changeHomeworkSharingDb(homework, isPublicOrVisible)
        } else {
            homeworkRepository.changeHomeworkVisibilityDb(homework, !isPublicOrVisible)
        }
        return true
    }
}
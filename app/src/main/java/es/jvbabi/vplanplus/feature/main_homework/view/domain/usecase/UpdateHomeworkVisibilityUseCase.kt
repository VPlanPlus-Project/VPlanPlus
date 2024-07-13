package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class UpdateHomeworkVisibilityUseCase(
    private val homeworkRepository: HomeworkRepository,
) {
    suspend operator fun invoke(personalizedHomework: PersonalizedHomework.CloudHomework, isPublicOrVisible: Boolean): Boolean {
        val profile = personalizedHomework.profile
        val homework = personalizedHomework.homework
        val isOwner = homework.createdBy.id == profile.vppId?.id

        if (isOwner) {
            homeworkRepository.changeHomeworkSharingCloud(personalizedHomework, isPublicOrVisible).value ?: return false
            homeworkRepository.changeHomeworkSharingDb(homework, isPublicOrVisible)
        } else {
            homeworkRepository.changeHomeworkVisibilityDb(personalizedHomework, !isPublicOrVisible)
        }
        return true
    }
}
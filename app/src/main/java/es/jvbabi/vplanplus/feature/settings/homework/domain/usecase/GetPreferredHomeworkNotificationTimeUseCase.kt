package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository

class GetPreferredHomeworkNotificationTimeUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    operator fun invoke() = homeworkRepository.getPreferredHomeworkNotificationTimes()
}
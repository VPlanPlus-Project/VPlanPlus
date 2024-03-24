package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class GetPreferredHomeworkNotificationTimeUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    operator fun invoke() = homeworkRepository.getPreferredHomeworkNotificationTimes()
}
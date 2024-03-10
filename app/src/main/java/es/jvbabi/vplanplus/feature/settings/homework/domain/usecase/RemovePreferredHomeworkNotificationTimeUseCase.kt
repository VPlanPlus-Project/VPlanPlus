package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import java.time.DayOfWeek

class RemovePreferredHomeworkNotificationTimeUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(dayOfWeek: DayOfWeek) {
        homeworkRepository.removePreferredHomeworkNotificationTime(dayOfWeek)
    }
}
package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import java.time.DayOfWeek

class SetPreferredHomeworkNotificationTimeUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val setUpUseCase: SetUpUseCase
) {
    suspend operator fun invoke(dayOfWeek: DayOfWeek, hour: Int, minute: Int) {
        homeworkRepository.setPreferredHomeworkNotificationTime(
            hour = hour,
            minute = minute,
            dayOfWeek = dayOfWeek
        )
        setUpUseCase.createHomeworkReminder()
    }
}
package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class UpdateDueDateUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(homework: Homework, newDate: LocalDate): HomeworkModificationResult {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return HomeworkModificationResult.FAILED
        val date = ZonedDateTime.of(newDate, LocalTime.of(0, 0, 0), ZoneId.of("UTC"))
        return homeworkRepository.updateDueDate(profile, homework, date)
    }
}
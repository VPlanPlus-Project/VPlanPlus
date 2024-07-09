package es.jvbabi.vplanplus.feature.main_homework.list_old.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
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
    suspend operator fun invoke(homework: Homework, newDate: LocalDate): Boolean {
        val vppId = (getCurrentProfileUseCase().first() as? ClassProfile ?: return false).vppId
        val date = ZonedDateTime.of(newDate, LocalTime.of(0, 0, 0), ZoneId.of("UTC"))
        if (homework is Homework.CloudHomework && vppId != null) homeworkRepository.changeDueDateCloud(vppId, homework, date).value ?: return false
        homeworkRepository.changeDueDateDb(homework, date)
        return true
    }
}
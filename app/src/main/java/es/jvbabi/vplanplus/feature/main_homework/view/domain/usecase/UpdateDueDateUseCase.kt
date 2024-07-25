package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class UpdateDueDateUseCase(
    private val homeworkRepository: HomeworkRepository,
) {
    suspend operator fun invoke(personalizedHomework: PersonalizedHomework, newDate: LocalDate): Boolean {
        val vppId = personalizedHomework.profile.vppId
        val homework = personalizedHomework.homework
        val date = ZonedDateTime.of(newDate, LocalTime.of(0, 0, 0), ZoneId.of("UTC"))
        if (personalizedHomework is PersonalizedHomework.CloudHomework && vppId != null) homeworkRepository.changeDueDateCloud(personalizedHomework, date) ?: return false
        homeworkRepository.changeDueDateDb(homework, date)
        return true
    }
}
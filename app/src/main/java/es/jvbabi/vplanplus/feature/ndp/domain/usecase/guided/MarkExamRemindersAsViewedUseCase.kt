package es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class MarkExamRemindersAsViewedUseCase(
    private val examRepository: ExamRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
){
    suspend operator fun invoke(exams: List<Exam>) {
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return
        exams.forEach { exam ->
            examRepository.updateExamLocally(
                exam.let {
                    it.copy(assessmentReminders = it.assessmentReminders.map { reminder ->
                        if (reminder.profile.id == profile.id && reminder.daysBefore == LocalDate.now().until(it.date).days) reminder.copy(hasDismissed = true) else reminder
                    }.toSet())
                },
                profile
            )
        }
    }
}
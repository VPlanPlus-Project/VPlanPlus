package es.jvbabi.vplanplus.feature.exams.domain.usecase.details

import es.jvbabi.vplanplus.domain.model.AssessmentReminder
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.first

class UpdateExamReminderDaysUseCase(
    private val examRepository: ExamRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(
        examId: Int,
        newDays: Set<Int>
    ) {
        val currentProfile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return
        val exam = examRepository.getExamById(examId).first() ?: return

        examRepository.updateExamLocally(exam.copy(
            assessmentReminders = exam.assessmentReminders
                .filter { it.profile.id != currentProfile.id || it.daysBefore in newDays }
                .plus(newDays.filter { daysBefore -> daysBefore !in exam.assessmentReminders.filter { it.profile.id == currentProfile.id }.map { it.daysBefore } }.map { AssessmentReminder(profile = currentProfile, daysBefore = it) })
                .toSet()
        ), currentProfile)
    }
}
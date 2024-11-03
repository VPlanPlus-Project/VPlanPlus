package es.jvbabi.vplanplus.feature.exams.domain.usecase.details

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class UpdateExamDateUseCase(
    private val examRepository: ExamRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(
        examId: Int,
        newDate: LocalDate
    ): Boolean {
        val currentProfile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return false

        val exam = examRepository.getExamById(examId).first() ?: return false

        return examRepository.updateExam(exam.copy(date = newDate), currentProfile).isSuccess
    }
}
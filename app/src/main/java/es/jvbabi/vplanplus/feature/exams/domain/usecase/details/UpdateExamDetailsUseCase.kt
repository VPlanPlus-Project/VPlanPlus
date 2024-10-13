package es.jvbabi.vplanplus.feature.exams.domain.usecase.details

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.first

class UpdateExamDetailsUseCase(
    private val examRepository: ExamRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(
        examId: Int,
        newDetails: String?
    ) {
        val currentProfile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return

        val exam = examRepository.getExamById(examId).first()

        if (exam.id > 0) {
            TODO("Online")
        }

        examRepository.updateExamLocally(exam.copy(description = newDetails), currentProfile)
    }
}
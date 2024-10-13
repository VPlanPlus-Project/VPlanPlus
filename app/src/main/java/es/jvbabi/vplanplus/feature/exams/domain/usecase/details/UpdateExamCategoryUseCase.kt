package es.jvbabi.vplanplus.feature.exams.domain.usecase.details

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.first

class UpdateExamCategoryUseCase(
    private val examRepository: ExamRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(
        examId: Int,
        newCategory: ExamType
    ) {
        val currentProfile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return

        val exam = examRepository.getExamById(examId).first()

        if (exam.id > 0) {
            TODO("Online")
        }

        examRepository.updateExamLocally(exam.copy(type = newCategory))
    }
}
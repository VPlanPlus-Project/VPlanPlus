package es.jvbabi.vplanplus.feature.exams.domain.usecase.details

import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository

class GetExamUseCase(
    private val examRepository: ExamRepository
) {
    operator fun invoke(examId: Int) = examRepository.getExamById(examId)
}
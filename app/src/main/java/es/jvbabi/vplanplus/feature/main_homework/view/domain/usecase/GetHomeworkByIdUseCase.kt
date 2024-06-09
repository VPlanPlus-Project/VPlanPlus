package es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class GetHomeworkByIdUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke(homeworkId: Int) = homeworkRepository.getHomeworkById(homeworkId)
}
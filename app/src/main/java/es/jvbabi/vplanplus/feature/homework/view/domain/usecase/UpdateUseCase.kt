package es.jvbabi.vplanplus.feature.homework.view.domain.usecase

import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository

class UpdateUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke() {
        homeworkRepository.fetchHomework()
    }
}
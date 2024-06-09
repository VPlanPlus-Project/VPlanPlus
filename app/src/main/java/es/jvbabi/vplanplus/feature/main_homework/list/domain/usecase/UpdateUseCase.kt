package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class UpdateUseCase(
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke() {
        homeworkRepository.fetchHomework(false)
    }
}
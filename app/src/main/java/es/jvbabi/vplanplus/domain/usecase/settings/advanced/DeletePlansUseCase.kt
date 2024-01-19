package es.jvbabi.vplanplus.domain.usecase.settings.advanced

import es.jvbabi.vplanplus.domain.repository.LessonRepository

class DeletePlansUseCase(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke() {
        lessonRepository.deleteAllLessons()
    }
}
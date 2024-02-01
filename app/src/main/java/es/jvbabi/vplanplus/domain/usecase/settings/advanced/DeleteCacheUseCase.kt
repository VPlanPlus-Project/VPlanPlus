package es.jvbabi.vplanplus.domain.usecase.settings.advanced

import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository

class DeleteCacheUseCase(
    private val lessonRepository: LessonRepository,
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke() {
        lessonRepository.deleteAllLessons()
        roomRepository.deleteAllRoomBookings()
    }
}
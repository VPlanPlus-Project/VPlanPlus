package es.jvbabi.vplanplus.domain.usecase.settings.advanced

import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository

class DeleteCacheUseCase(
    private val lessonRepository: LessonRepository,
    private val roomRepository: RoomRepository,
    private val gradeRepository: GradeRepository
) {
    suspend operator fun invoke() {
        lessonRepository.deleteAllLessons()
        roomRepository.deleteAllRoomBookings()
        gradeRepository.dropAll()
    }
}
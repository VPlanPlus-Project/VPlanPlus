package es.jvbabi.vplanplus.domain.usecase.settings.advanced

import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository

class DeleteCacheUseCase(
    private val lessonRepository: LessonRepository,
    private val roomRepository: RoomRepository,
    private val gradeRepository: GradeRepository,
    private val homeworkRepository: HomeworkRepository
) {
    suspend operator fun invoke() {
        lessonRepository.deleteAllLessons()
        roomRepository.deleteAllRoomBookings()
        gradeRepository.dropAll()
        homeworkRepository.clearCache()
    }
}
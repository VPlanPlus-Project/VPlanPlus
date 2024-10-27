package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class DeleteCacheUseCase(
    private val lessonRepository: LessonRepository,
    private val roomRepository: RoomRepository,
    private val gradeRepository: GradeRepository,
    private val homeworkRepository: HomeworkRepository,
    private val timetableRepository: TimetableRepository,
    private val holidayRepository: HolidayRepository
) {
    suspend operator fun invoke() {
        lessonRepository.deleteAllLessons()
        roomRepository.deleteAllRoomBookings()
        gradeRepository.dropAll()
        homeworkRepository.clearCache()
        timetableRepository.clearCache()
        holidayRepository.deleteAll()
    }
}
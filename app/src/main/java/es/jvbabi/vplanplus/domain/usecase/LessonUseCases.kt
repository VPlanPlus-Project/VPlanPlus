package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import java.time.LocalDate

class LessonUseCases(
    private val lessonRepository: LessonRepository
) {

    suspend fun getTodayLessonForClass(classId: Long): List<Lesson> {
        return lessonRepository.getLessonsForClass(classId = classId, LocalDate.now())
    }
}
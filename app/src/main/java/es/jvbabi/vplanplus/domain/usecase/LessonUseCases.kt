package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import java.time.LocalDate

class LessonUseCases(
    private val lessonRepository: LessonRepository
) {

    suspend fun getTodayLessonForClass(classId: Int): List<Pair<Lesson, DefaultLesson?>> {
        return lessonRepository.getLessonsForClass(classId = classId, LocalDate.now())
    }
}
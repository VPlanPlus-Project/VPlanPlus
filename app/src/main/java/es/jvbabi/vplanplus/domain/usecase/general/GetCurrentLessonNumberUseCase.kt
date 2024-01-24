package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import java.time.LocalDateTime

class GetCurrentLessonNumberUseCase(
    private val lessonTimeRepository: LessonTimeRepository
) {
    /**
     * Returns the current lesson number, or null if there is no lesson at the moment.
     * @return The current lesson number, or null if there is no lesson at the moment. If there's a break, it returns last lesson number + 0.5
     */
    suspend operator fun invoke(`class`: Classes): Double? {
        val lessonTimes = lessonTimeRepository.getLessonTimesByClass(`class`)
        var currentLesson = 0.5
        val now = LocalDateTime.now().withDayOfYear(1).withYear(1970)
        lessonTimes.forEach { lessonTime ->
            if (now.isAfter(lessonTime.value.start) || now.isEqual(lessonTime.value.start)) currentLesson += 0.5
            if (now.isAfter(lessonTime.value.end) || now.isEqual(lessonTime.value.end)) currentLesson += 0.5
        }
        return if (currentLesson == 0.5) null else currentLesson-1
    }
}
package es.jvbabi.vplanplus.util

import es.jvbabi.vplanplus.domain.model.LessonTime
import java.time.LocalDateTime
import java.util.UUID

object LessonTime {
    fun fallbackTime(classId: UUID, lessonNumber: Int): LessonTime {
        return LessonTime(
            classLessonTimeRefId = classId,
            lessonNumber = lessonNumber,
            start = LocalDateTime.of(1970, 1, 1, 0, 0),
            end = LocalDateTime.of(1970, 1, 1, 23, 59, 59)
        )
    }
}
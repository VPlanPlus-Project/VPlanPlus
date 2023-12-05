package es.jvbabi.vplanplus.util

import es.jvbabi.vplanplus.domain.model.LessonTime
import java.util.UUID

object LessonTime {
    fun fallbackTime(classId: UUID, lessonNumber: Int): LessonTime {
        return LessonTime(
            classLessonTimeRefId = classId,
            lessonNumber = lessonNumber,
            start = "00:00",
            end = "23:59"
        )
    }
}
package es.jvbabi.vplanplus.util

import es.jvbabi.vplanplus.domain.model.LessonTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

object LessonTime {
    fun fallbackTime(classId: UUID, lessonNumber: Int): LessonTime {
        return LessonTime(
            classLessonTimeRefId = classId,
            lessonNumber = lessonNumber,
            from = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Berlin")),
            to = ZonedDateTime.of(1970, 1, 1, 23, 59, 59, 99, ZoneId.of("Europe/Berlin")),
        )
    }
}
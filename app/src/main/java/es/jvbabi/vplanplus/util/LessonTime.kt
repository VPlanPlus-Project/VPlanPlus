package es.jvbabi.vplanplus.util

import es.jvbabi.vplanplus.domain.model.LessonTime

object LessonTime {
    fun fallbackTime(classId: Long, lessonNumber: Int): LessonTime {
        return LessonTime(
            classLessonTimeRefId = classId,
            lessonNumber = lessonNumber,
            start = "00:00",
            end = "23:59"
        )
    }
}
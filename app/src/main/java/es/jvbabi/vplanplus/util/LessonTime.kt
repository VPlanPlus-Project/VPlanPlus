package es.jvbabi.vplanplus.util

import es.jvbabi.vplanplus.domain.model.LessonTime

object LessonTime {
    fun fallbackTime(classId: Long, lessonNumber: Int): LessonTime {
        return LessonTime(
            null,
            classId,
            lessonNumber,
            "00:00",
            "23:59"
        )
    }
}
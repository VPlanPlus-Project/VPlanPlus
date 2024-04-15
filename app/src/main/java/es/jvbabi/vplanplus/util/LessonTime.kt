package es.jvbabi.vplanplus.util

import es.jvbabi.vplanplus.domain.model.LessonTime
import java.util.UUID

object LessonTime {
    fun fallbackTime(classId: UUID, lessonNumber: Int): LessonTime {
        return LessonTime(
            classLessonTimeRefId = classId,
            lessonNumber = lessonNumber,
            from = (7*60*60)+(30*60), // 7:30
            to = (8*60*60)+(15*60) // 8:15
        )
    }
}
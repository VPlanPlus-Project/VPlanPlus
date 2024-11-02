package es.jvbabi.vplanplus.util

import es.jvbabi.vplanplus.domain.model.LessonTime

object LessonTime {
    fun fallbackTime(classId: Int, lessonNumber: Int, otherLessonTimes: List<LessonTime>): LessonTime {
        return otherLessonTimes.find { it.lessonNumber == lessonNumber }?: run {
            val previous = otherLessonTimes.firstOrNull { it.lessonNumber == lessonNumber - 1 } ?: return@run LessonTime(
                groupId = classId,
                lessonNumber = lessonNumber,
                from = (7*60*60)+(30*60), // 7:30
                to = (8*60*60)+(15*60) // 8:15
            )
            val startSeconds = (previous.start.hour * 60 + previous.start.minute) * 60L
            val endSeconds = (previous.end.hour * 60 + previous.end.minute) * 60L
            return LessonTime(
                groupId = classId,
                lessonNumber = lessonNumber,
                from = endSeconds,
                to = endSeconds + (endSeconds-startSeconds)
            )
        }
    }
}
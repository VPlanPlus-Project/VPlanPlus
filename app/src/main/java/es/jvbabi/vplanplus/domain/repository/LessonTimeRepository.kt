package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.LessonTime

interface LessonTimeRepository {
    suspend fun insertLessonTime(lessonTime: LessonTime)
    suspend fun deleteLessonTimes(group: Group)
    suspend fun getLessonTimesByGroup(group: Group): Map<Int, LessonTime>

    /**
     * Inserts a lesson time.
     * @param groupId The group id.
     * @param lessonNumber The lesson number.
     * @param from The start time of the lesson, using the 1970-01-01T00:00:00Z as date.
     * @param to The end time of the lesson, using the 1970-01-01T00:00:00Z as date.
     */
    suspend fun insertLessonTime(groupId: Int, lessonNumber: Int, from: Long, to: Long)
}
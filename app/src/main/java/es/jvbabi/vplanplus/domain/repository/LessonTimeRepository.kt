package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.LessonTime

interface LessonTimeRepository {
    suspend fun insertLessonTime(lessonTime: LessonTime)
    suspend fun deleteLessonTimes(classId: Int)
    suspend fun getLessonTimesByClassId(classId: Int): List<LessonTime>
}
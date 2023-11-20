package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.LessonTime

interface LessonTimeRepository {
    suspend fun insertLessonTime(lessonTime: LessonTime)
    suspend fun deleteLessonTimes(`class`: Classes)
    suspend fun getLessonTimesByClass(`class`: Classes): Map<Int, LessonTime>
}
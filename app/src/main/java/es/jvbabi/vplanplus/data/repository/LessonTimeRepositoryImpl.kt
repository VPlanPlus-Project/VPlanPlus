package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.LessonTimeDao
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository

class LessonTimeRepositoryImpl(
    private val lessonTimeDao: LessonTimeDao
) : LessonTimeRepository {
    override suspend fun insertLessonTime(lessonTime: LessonTime) {
        lessonTimeDao.insertLessonTime(lessonTime)
    }

    override suspend fun deleteLessonTimes(classId: Int) {
        lessonTimeDao.deleteLessonTimes(classId)
    }

    override suspend fun getLessonTimesByClassId(classId: Int): List<LessonTime> {
        return lessonTimeDao.getLessonTimesByClassId(classId)
    }
}
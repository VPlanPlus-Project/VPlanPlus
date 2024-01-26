package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.LessonTimeDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository

class LessonTimeRepositoryImpl(
    private val lessonTimeDao: LessonTimeDao
) : LessonTimeRepository {
    override suspend fun insertLessonTime(lessonTime: LessonTime) {
        lessonTimeDao.insertLessonTime(lessonTime)
    }

    override suspend fun insertLessonTimes(lessonTimes: List<LessonTime>) {
        lessonTimeDao.insertLessonTimes(lessonTimes)
    }

    override suspend fun deleteLessonTimes(`class`: Classes) {
        lessonTimeDao.deleteLessonTimes(`class`.classId)
    }

    override suspend fun getLessonTimesByClass(`class`: Classes): Map<Int, LessonTime> {
        return lessonTimeDao.getLessonTimesByClassId(`class`.classId).associateBy {
            it.lessonNumber
        }
    }
}
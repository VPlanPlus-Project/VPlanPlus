package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.LessonTimeDao
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository

class LessonTimeRepositoryImpl(
    private val lessonTimeDao: LessonTimeDao
) : LessonTimeRepository {
    override suspend fun insertLessonTime(lessonTime: LessonTime) {
        lessonTimeDao.insertLessonTime(lessonTime)
    }

    override suspend fun deleteLessonTimes(group: Group) {
        lessonTimeDao.deleteLessonTimes(group.groupId)
    }

    override suspend fun getLessonTimesByGroup(group: Group): Map<Int, LessonTime> {
        return lessonTimeDao.getLessonTimesByGroupId(group.groupId).associateBy {
            it.lessonNumber
        }
    }

    override suspend fun insertLessonTime(groupId: Int, lessonNumber: Int, from: Long, to: Long) {
        lessonTimeDao.insertLessonTime(
            LessonTime(
                groupId = groupId,
                lessonNumber = lessonNumber,
                from = from,
                to = to
            )
        )
    }
}
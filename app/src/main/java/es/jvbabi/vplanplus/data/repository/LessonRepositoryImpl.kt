package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.LessonDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

class LessonRepositoryImpl(
    private val lessonDao: LessonDao
): LessonRepository {
    override suspend fun getLessonsForClass(classId: Long, date: LocalDate): List<Lesson> {
        return lessonDao.getLessonsByClass(
            classId, DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        )
    }

    override suspend fun getLessonsForTeacher(teacherId: Long, date: LocalDate): List<Lesson> {
        return lessonDao.getLessonsByTeacher(
            teacherId, DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        )
    }

    override suspend fun getLessonsForRoom(roomId: Long, date: LocalDate): List<Lesson> {
        return lessonDao.getLessonsByRoom(
            roomId, DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        )
    }

    override suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate) {
        lessonDao.deleteLessonByClassIdAndTimestamp(`class`.id!!, DateUtils.getDayTimestamp(
            year = date.year,
            month = date.monthValue,
            day = date.dayOfMonth
        ))
    }

    override suspend fun insertLesson(lesson: Lesson) {
        lessonDao.insert(lesson)
    }
}
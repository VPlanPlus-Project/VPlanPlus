package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.DefaultLessonDao
import es.jvbabi.vplanplus.data.source.LessonDao
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

class LessonRepositoryImpl(
    private val defaultLessonDao: DefaultLessonDao,
    private val lessonDao: LessonDao
): LessonRepository {
    override suspend fun getLessonsForClass(classId: Int, date: LocalDate): List<Pair<Lesson, DefaultLesson?>> {
        val lessons = lessonDao.getLessonsByClass(classId, DateUtils.getDayTimestamp(
            year = date.year,
            month = date.monthValue,
            day = date.dayOfMonth
        ))
        val result = mutableListOf<Pair<Lesson, DefaultLesson?>>()
        lessons.forEach {
            if (it.defaultLessonId == null) result.add(it to null as DefaultLesson?)
            else result.add(Pair(it, defaultLessonDao.getDefaultLessonById(it.defaultLessonId)))
        }
        return result
    }

    override suspend fun getLessonsForTeacher(teacherId: Int, date: LocalDate): List<Pair<Lesson, DefaultLesson?>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLessonsForRoom(roomId: Int, date: LocalDate): List<Pair<Lesson, DefaultLesson?>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLessonForClass(classId: Int, date: LocalDate) {
        lessonDao.deleteLessonByClassIdAndTimestamp(classId, DateUtils.getDayTimestamp(
            year = date.year,
            month = date.monthValue,
            day = date.dayOfMonth
        ))
    }

    override suspend fun insertLesson(lesson: Lesson) {
        lessonDao.insert(lesson)
    }
}
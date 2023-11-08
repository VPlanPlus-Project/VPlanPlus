package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.LessonDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonRoomCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTeacherCrossoverDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

class LessonRepositoryImpl(
    private val lessonDao: LessonDao,
    private val lessonRoomCrossoverDao: LessonRoomCrossoverDao,
    private val lessonTeacherCrossoverDao: LessonTeacherCrossoverDao,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository
): LessonRepository {
    override suspend fun getLessonsForClass(classId: Long, date: LocalDate): List<Lesson> {
        return lessonDao.getLessonsByClass(
            classId, DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        ).onEach { lesson ->
            lesson.rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.id!!).map { roomRepository.getRoomById(it) }
            lesson.teachers = lessonTeacherCrossoverDao.getTeacherIdsByLessonId(lesson.id).map { teacherRepository.getTeacherById(it)!! }
        }
    }

    override suspend fun getLessonsForTeacher(teacherId: Long, date: LocalDate): List<Lesson> {
        return lessonDao.getLessonsByTeacher(
            teacherId, DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        ).onEach { lesson ->
            lesson.rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.id!!).map { roomRepository.getRoomById(it) }
        }
    }

    override suspend fun getLessonsForRoom(roomId: Long, date: LocalDate): List<Lesson> {
        return lessonDao.getLessonsByRoom(
            roomId,
            DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        ).onEach { lesson ->
            lesson.rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.id!!).map { roomRepository.getRoomById(it) }
        }
    }

    override suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate) {
        lessonDao.deleteLessonByClassIdAndTimestamp(`class`.id!!, DateUtils.getDayTimestamp(
            year = date.year,
            month = date.monthValue,
            day = date.dayOfMonth
        ))
    }

    override suspend fun insertLesson(lesson: Lesson) {
        val lessonId = lessonDao.insert(lesson)
        lesson.rooms.forEach { room ->
            lessonRoomCrossoverDao.insertCrossover(
                lessonId = lessonId,
                roomId = room.id!!
            )
        }
        lesson.teachers.forEach { teacher ->
            lessonTeacherCrossoverDao.insertCrossover(
                lessonId = lessonId,
                teacherId = teacher.id!!
            )
        }
    }
}
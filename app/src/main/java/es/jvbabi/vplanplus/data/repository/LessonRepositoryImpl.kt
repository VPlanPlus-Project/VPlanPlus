package es.jvbabi.vplanplus.data.repository

import android.util.Log
import es.jvbabi.vplanplus.data.source.database.dao.ClassDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonRoomCrossoverDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

class LessonRepositoryImpl(
    private val lessonDao: LessonDao,
    private val classDao: ClassDao,
    private val lessonRoomCrossoverDao: LessonRoomCrossoverDao,
    private val roomRepository: RoomRepository
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
        val room = roomRepository.getRoomById(roomId)
        return lessonDao.getAllLessons(
            DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        ).filter {
            val `class` = classDao.getClassById(it.classId)
            `class`.schoolId == room.schoolId
        }.onEach { lesson ->
            lesson.rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.id!!).map { roomRepository.getRoomById(it) }
            if (lesson.originalSubject == "INF") Log.d("LessonRepositoryImpl", "lesson=${lesson.id}")
        }.filter { lesson ->
            lesson.rooms.any { it.id == roomId }
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
    }
}
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@ExperimentalCoroutinesApi
class LessonRepositoryImpl(
    private val lessonDao: LessonDao,
    private val lessonRoomCrossoverDao: LessonRoomCrossoverDao,
    private val lessonTeacherCrossoverDao: LessonTeacherCrossoverDao,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository
) : LessonRepository {

    override fun getLessonsForClass(classId: Long, date: LocalDate): Flow<List<Lesson>> {
        return lessonDao.getLessonsByClass(
            classId, DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        ).map { lessons ->
            lessons.onEach { lesson ->
                lesson.rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.id!!)
                    .map { roomRepository.getRoomById(it) }
                lesson.teachers = lessonTeacherCrossoverDao.getTeacherIdsByLessonId(lesson.id)
                    .map { teacherRepository.getTeacherById(it)!! }
            }
        }
    }

    override fun getLessonsForTeacher(teacherId: Long, date: LocalDate): Flow<List<Lesson>> {
        return lessonDao.getLessonsByTeacher(
            teacherId, DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        ).map { lessons ->
            lessons.onEach { lesson ->
                lesson.rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.id!!)
                    .map { roomRepository.getRoomById(it) }
            }
        }
    }

    override fun getLessonsForRoom(roomId: Long, date: LocalDate): Flow<List<Lesson>> {
        return lessonDao.getLessonsByRoom(
            roomId,
            DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        ).map { lessons ->
            lessons.onEach { lesson ->
                lesson.teachers = lessonTeacherCrossoverDao.getTeacherIdsByLessonId(lesson.id!!)
                    .map { teacherRepository.getTeacherById(it)!! }
            }
        }
    }

    override suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate) {
        lessonDao.deleteLessonByClassIdAndTimestamp(
            `class`.id!!, DateUtils.getDayTimestamp(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            )
        )
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

    override suspend fun deleteAllLessons() {
        lessonDao.deleteAll()
    }
}
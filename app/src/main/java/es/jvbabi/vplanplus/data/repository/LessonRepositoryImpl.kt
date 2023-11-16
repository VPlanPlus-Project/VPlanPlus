package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.source.database.dao.DbLessonDao
import es.jvbabi.vplanplus.data.source.database.dao.DefaultLessonDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonRoomCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTeacherCrossoverDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.ui.screens.home.DayType
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.LessonTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@ExperimentalCoroutinesApi
class LessonRepositoryImpl(
    private val lessonRoomCrossoverDao: LessonRoomCrossoverDao,
    private val lessonTeacherCrossoverDao: LessonTeacherCrossoverDao,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository,
    private val schoolRepository: SchoolRepository,
    private val classRepository: ClassRepository,
    private val holidayRepository: HolidayRepository,
    private val defaultLessonDao: DefaultLessonDao,
    private val lessonDao: DbLessonDao,
    private val lessonTimeRepository: LessonTimeRepository
) : LessonRepository {

    override fun getLessonsForClass(classId: Long, date: LocalDate): Flow<Pair<DayType, List<Lesson>>> {
        // if there won't be any lessons for this date
        val `class` = classRepository.getClassById(classId)
        if (date.dayOfWeek.value > `class`.school.daysPerWeek) return flowOf(DayType.WEEKEND to emptyList())
        if (holidayRepository.isHoliday(`class`.school.schoolId, date)) return flowOf(DayType.HOLIDAY to emptyList())

        return lessonDao.getLessonsByClass(classId, date)
            .map { lessons ->
                DayType.DATA to lessons.map { lesson ->
                    val defaultLesson = if (lesson.defaultLessonId == null) null else defaultLessonDao.getDefaultLessonById(lesson.defaultLessonId)
                    val lessonTime = lessonTimeRepository.getLessonTimesByClass(`class`)[lesson.lessonNumber] ?: LessonTime.fallbackTime(`class`.classId, lesson.lessonNumber)
                    val teachers = lessonTeacherCrossoverDao.getTeacherIdsByLessonId(lesson.lessonId).mapNotNull { teacherRepository.getTeacherById(it) }
                    val rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.lessonId).map { roomRepository.getRoomById(it) }
                    Lesson(
                        `class` = `class`,
                        lessonNumber = lesson.lessonNumber,
                        originalSubject = defaultLesson?.subject,
                        changedSubject = lesson.changedSubject,
                        roomIsChanged = lesson.roomIsChanged,
                        start = DateUtils.getLocalDateTimeFromLocalDateAndTimeString(localDate = date, timeString = lessonTime.start),
                        end = DateUtils.getLocalDateTimeFromLocalDateAndTimeString(localDate = date, timeString = lessonTime.end),
                        info = lesson.info,
                        day = date,
                        teachers = teachers,
                        teacherIsChanged = teachers.map { it.teacherId }.sorted() != listOf(defaultLesson?.teacherId),
                        rooms = rooms
                    )
                }
            }
    }

    override fun getLessonsForTeacher(teacherId: Long, date: LocalDate): Flow<Pair<DayType, List<Lesson>>> {
        // if there won't be any lessons for this date
        val teacher = teacherRepository.getTeacherById(teacherId)!!
        val school = schoolRepository.getSchoolFromId(teacher.schoolTeacherRefId)
        if (date.dayOfWeek.value > school.daysPerWeek) return flowOf(DayType.WEEKEND to emptyList())
        if (holidayRepository.isHoliday(school.schoolId, date)) return flowOf(DayType.HOLIDAY to emptyList())

        return lessonDao.getLessonsByTeacher(teacherId, date)
            .map { lessons ->
                DayType.DATA to lessons.map { lesson ->
                    val defaultLesson = if (lesson.defaultLessonId == null) null else defaultLessonDao.getDefaultLessonById(lesson.defaultLessonId)
                    val `class` = classRepository.getClassById(lesson.classLessonRefId)
                    val lessonTime = lessonTimeRepository.getLessonTimesByClass(`class`)[lesson.lessonNumber] ?: LessonTime.fallbackTime(`class`.classId, lesson.lessonNumber)
                    val teachers = lessonTeacherCrossoverDao.getTeacherIdsByLessonId(lesson.lessonId).mapNotNull { teacherRepository.getTeacherById(it) }
                    val rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.lessonId).map { roomRepository.getRoomById(it) }
                    Lesson(
                        `class` = `class`,
                        lessonNumber = lesson.lessonNumber,
                        originalSubject = defaultLesson?.subject,
                        changedSubject = lesson.changedSubject,
                        roomIsChanged = lesson.roomIsChanged,
                        start = DateUtils.getLocalDateTimeFromLocalDateAndTimeString(localDate = date, timeString = lessonTime.start),
                        end = DateUtils.getLocalDateTimeFromLocalDateAndTimeString(localDate = date, timeString = lessonTime.end),
                        info = lesson.info,
                        day = date,
                        teachers = teachers,
                        teacherIsChanged = teachers.map { it.teacherId }.sorted() != listOf(defaultLesson?.teacherId),
                        rooms = rooms
                    )
                }
            }
    }

    override fun getLessonsForRoom(roomId: Long, date: LocalDate): Flow<Pair<DayType, List<Lesson>>> {
        // if there won't be any lessons for this date
        val room = roomRepository.getRoomById(roomId)
        val school = schoolRepository.getSchoolFromId(room.schoolRoomRefId)
        if (date.dayOfWeek.value > school.daysPerWeek) return flowOf(DayType.WEEKEND to emptyList())
        if (holidayRepository.isHoliday(school.schoolId, date)) return flowOf(DayType.HOLIDAY to emptyList())

        return lessonDao.getLessonsByRoom(roomId, date)
            .map { lessons ->
                DayType.DATA to lessons.map { lesson ->
                    val defaultLesson = if (lesson.defaultLessonId == null) null else defaultLessonDao.getDefaultLessonById(lesson.defaultLessonId)
                    val `class` = classRepository.getClassById(lesson.classLessonRefId)
                    val lessonTime = lessonTimeRepository.getLessonTimesByClass(`class`)[lesson.lessonNumber] ?: LessonTime.fallbackTime(`class`.classId, lesson.lessonNumber)
                    val teachers = lessonTeacherCrossoverDao.getTeacherIdsByLessonId(lesson.lessonId).mapNotNull { teacherRepository.getTeacherById(it) }
                    val rooms = lessonRoomCrossoverDao.getRoomIdsByLessonId(lesson.lessonId).map { roomRepository.getRoomById(it) }
                    Lesson(
                        `class` = `class`,
                        lessonNumber = lesson.lessonNumber,
                        originalSubject = defaultLesson?.subject,
                        changedSubject = lesson.changedSubject,
                        roomIsChanged = lesson.roomIsChanged,
                        start = DateUtils.getLocalDateTimeFromLocalDateAndTimeString(localDate = date, timeString = lessonTime.start),
                        end = DateUtils.getLocalDateTimeFromLocalDateAndTimeString(localDate = date, timeString = lessonTime.end),
                        info = lesson.info,
                        day = date,
                        teachers = teachers,
                        teacherIsChanged = teachers.map { it.teacherId }.sorted() != listOf(defaultLesson?.teacherId),
                        rooms = rooms
                    )
                }
            }
    }

    override suspend fun deleteLessonForClass(`class`: Classes, date: LocalDate) {
        lessonDao.deleteLessonsByClassAndDate(`class`.classId, date)
    }

    override suspend fun insertLesson(dbLesson: DbLesson): Long {
        return lessonDao.insertLesson(dbLesson)
    }

    override suspend fun deleteAllLessons() {
        lessonDao.deleteAll()
    }
}
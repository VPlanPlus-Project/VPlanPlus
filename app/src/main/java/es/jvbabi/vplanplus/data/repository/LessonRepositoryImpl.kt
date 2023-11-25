package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.source.database.dao.LessonDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.DayType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@ExperimentalCoroutinesApi
class LessonRepositoryImpl(
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository,
    private val classRepository: ClassRepository,
    private val holidayRepository: HolidayRepository,
    private val lessonDao: LessonDao,
    private val keyValueUseCases: KeyValueUseCases
) : LessonRepository {

    override suspend fun getLessonsForClass(classId: Long, date: LocalDate, version: Long?): Flow<Pair<DayType, List<Lesson>>> {
        // if there won't be any lessons for this date
        val `class` = classRepository.getClassById(classId)
        if (date.dayOfWeek.value > `class`.school.daysPerWeek) return flowOf(DayType.WEEKEND to emptyList())
        if (holidayRepository.isHoliday(`class`.school.schoolId, date)) return flowOf(DayType.HOLIDAY to emptyList())

        return lessonDao.getLessonsByClass(classId, date, version?:keyValueUseCases.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong())
            .map { lessons ->
                DayType.DATA to lessons.map { lesson ->
                    lesson.toModel()
                }
            }
    }

    override suspend fun getLessonsForTeacher(teacherId: Long, date: LocalDate, version: Long?): Flow<Pair<DayType, List<Lesson>>> {
        // if there won't be any lessons for this date
        val teacher = teacherRepository.getTeacherById(teacherId)!!
        if (date.dayOfWeek.value > teacher.school.daysPerWeek) return flowOf(DayType.WEEKEND to emptyList())
        if (holidayRepository.isHoliday(teacher.school.schoolId, date)) return flowOf(DayType.HOLIDAY to emptyList())

        return lessonDao.getLessonsByTeacher(teacherId, date, version?:keyValueUseCases.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong())
            .map { lessons ->
                DayType.DATA to lessons.map { lesson ->
                    lesson.toModel()
                }
            }
    }

    override suspend fun getLessonsForRoom(roomId: Long, date: LocalDate, version: Long?): Flow<Pair<DayType, List<Lesson>>> {
        // if there won't be any lessons for this date
        val room = roomRepository.getRoomById(roomId)
        if (date.dayOfWeek.value > room.school.daysPerWeek) return flowOf(DayType.WEEKEND to emptyList())
        if (holidayRepository.isHoliday(room.school.schoolId, date)) return flowOf(DayType.HOLIDAY to emptyList())

        return lessonDao.getLessonsByRoom(roomId, date, version?:keyValueUseCases.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong())
            .map { lessons ->
                DayType.DATA to lessons.map { lesson ->
                    lesson.toModel()
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

    override suspend fun getLessonsForClassDirect(
        classId: Long,
        date: LocalDate,
        version: Long?
    ): Pair<DayType, List<Lesson>> {
        return getLessonsForClass(classId, date, version).first()
    }

    override suspend fun getLessonsForTeacherDirect(
        teacherId: Long,
        date: LocalDate,
        version: Long?
    ): Pair<DayType, List<Lesson>> {
        return getLessonsForTeacher(teacherId, date, version).first()
    }

    override suspend fun getLessonsForRoomDirect(
        roomId: Long,
        date: LocalDate,
        version: Long?
    ): Pair<DayType, List<Lesson>> {
        return getLessonsForRoom(roomId, date, version).first()
    }
}
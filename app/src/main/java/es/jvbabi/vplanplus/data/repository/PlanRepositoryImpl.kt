package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.util.UUID

class PlanRepositoryImpl(
    private val holidayRepository: HolidayRepository,
    private val teacherRepository: TeacherRepository,
    private val classRepository: ClassRepository,
    private val roomRepository: RoomRepository,
    private val lessonRepository: LessonRepository
) : PlanRepository {
    override fun getDayForProfile(profile: Profile, date: LocalDate, version: Long): Flow<Day> {
        return when (profile.type) {
            ProfileType.STUDENT -> getDayForClass(profile.referenceId, date, version)
            ProfileType.TEACHER -> getDayForTeacher(profile.referenceId, date, version)
            ProfileType.ROOM -> getDayForRoom(profile.referenceId, date, version)
        }
    }

    override fun getDayForTeacher(teacherId: UUID, date: LocalDate, version: Long) = flow {
        val teacher = teacherRepository.getTeacherById(teacherId)!!
        val school = teacher.school

        lessonRepository.getLessonsForTeacher(teacherId, date, version).distinctUntilChanged().collect { lessons ->
            emit(build(school, lessons, date))
        }
    }

    override fun getDayForClass(classId: UUID, date: LocalDate, version: Long) = flow {
        val `class` = classRepository.getClassById(classId)
        val school = `class`.school

        lessonRepository.getLessonsForClass(`class`.classId, date, version).distinctUntilChanged().collect { lessons ->
            emit(build(school, lessons, date))
        }
    }

    override fun getDayForRoom(roomId: UUID, date: LocalDate, version: Long) = flow {
        val room = roomRepository.getRoomById(roomId)
        val school = room.school

        lessonRepository.getLessonsForRoom(room.roomId, date, version).distinctUntilChanged().collect { lessons ->
            emit(build(school, lessons, date))
        }
    }

    private fun build(school: School, lessons: List<Lesson>?, date: LocalDate): Day {
        val dayType = holidayRepository.getDayType(school.schoolId, date)
        if (dayType == DayType.NORMAL) {
            return if (lessons == null) {
                Day(
                    date = date,
                    type = dayType,
                    state = DayDataState.NO_DATA,
                    lessons = emptyList()
                )
            } else {
                Day(
                    date = date,
                    type = dayType,
                    state = DayDataState.DATA,
                    lessons = lessons
                )
            }
        } else {
            return Day(
                date = date,
                type = dayType,
                state = DayDataState.DATA,
                lessons = emptyList()
            )
        }
    }
}
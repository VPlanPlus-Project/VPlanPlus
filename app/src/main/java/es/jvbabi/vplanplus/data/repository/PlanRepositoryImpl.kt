package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.dao.PlanDao
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Plan
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomBooking
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
    private val lessonRepository: LessonRepository,
    private val planDao: PlanDao
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

        lessonRepository.getLessonsForTeacher(teacherId, date, version).distinctUntilChanged()
            .collect { lessons ->
                val bookings = roomRepository.getRoomBookings(date)
                emit(
                    build(
                        school,
                        lessons,
                        date,
                        planDao.getPlanByDate(school.schoolId, date)?.planData?.info,
                        bookings
                    )
                )
            }
    }

    override fun getDayForClass(classId: UUID, date: LocalDate, version: Long) = flow {
        val `class` = classRepository.getClassById(classId)!!
        val school = `class`.school

        lessonRepository.getLessonsForClass(`class`.classId, date, version).distinctUntilChanged()
            .collect { lessons ->
                val bookings = roomRepository.getRoomBookings(date)
                emit(
                    build(
                        school,
                        lessons,
                        date,
                        planDao.getPlanByDate(school.schoolId, date)?.planData?.info,
                        bookings
                    )
                )
            }
    }

    override fun getDayForRoom(roomId: UUID, date: LocalDate, version: Long) = flow {
        val room = roomRepository.getRoomById(roomId)!!
        val school = room.school

        lessonRepository.getLessonsForRoom(room.roomId, date, version).distinctUntilChanged()
            .collect { lessons ->
                val bookings = roomRepository.getRoomBookings(date)
                emit(
                    build(
                        school,
                        lessons,
                        date,
                        planDao.getPlanByDate(school.schoolId, date)?.planData?.info,
                        bookings
                    )
                )
            }
    }

    override suspend fun getLocalPlanDates(): List<LocalDate> {
        return planDao.getLocalPlanDates().distinct()
    }

    private suspend fun build(
        school: School,
        lessons: List<Lesson>?,
        date: LocalDate,
        info: String?,
        bookings: List<RoomBooking>
    ): Day {
        val dayType = holidayRepository.getDayType(school.schoolId, date)
        val lessonsWithBookings = lessons?.map { lesson ->
            val booking = bookings.firstOrNull { roomBooking ->
                roomBooking.`class` == lesson.`class` &&
                        lesson.start.isEqual(roomBooking.from) &&
                        lesson.end.isEqual(roomBooking.to.plusSeconds(1)) &&
                        date == roomBooking.from.toLocalDate()
            }
            lesson.copy(roomBooking = booking)
        }
        if (dayType == DayType.NORMAL) {
            return if (lessonsWithBookings == null) {
                Day(
                    date = date,
                    type = dayType,
                    state = DayDataState.NO_DATA,
                    lessons = emptyList(),
                    info = info
                )
            } else {
                Day(
                    date = date,
                    type = dayType,
                    state = DayDataState.DATA,
                    lessons = lessonsWithBookings,
                    info = info
                )
            }
        } else {
            return Day(
                date = date,
                type = dayType,
                state = DayDataState.DATA,
                lessons = emptyList(),
                info = info
            )
        }
    }

    override suspend fun createPlan(plan: Plan) {
        planDao.insertPlan(
            DbPlanData(
                id = UUID.randomUUID(),
                createDate = plan.createAt,
                schoolId = plan.school.schoolId,
                planDate = plan.date,
                info = plan.info,
                version = plan.version
            )
        )
    }

    override suspend fun deleteAllPlans() {
        planDao.deleteAllPlans()
    }

    override suspend fun deletePlansByVersion(version: Long) {
        planDao.deleteAllPlansByVersion(version)
    }
}
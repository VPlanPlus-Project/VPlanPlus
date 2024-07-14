package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.data.source.database.dao.PlanDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Plan
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.RoomProfile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.TeacherProfile
import es.jvbabi.vplanplus.domain.repository.GroupRepository
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
    private val groupRepository: GroupRepository,
    private val roomRepository: RoomRepository,
    private val lessonRepository: LessonRepository,
    private val planDao: PlanDao
) : PlanRepository {
    override fun getDayForProfile(profile: Profile, date: LocalDate, version: Long): Flow<Day> {
        return when (profile) {
            is ClassProfile -> getDayForGroup(profile.group.groupId, date, version)
            is TeacherProfile -> getDayForTeacher(profile.teacher.teacherId, date, version)
            is RoomProfile -> getDayForRoom(profile.room.roomId, date, version)
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
                        planDao.getPlanByDate(school.id, date)?.planData?.info,
                        bookings
                    )
                )
            }
    }

    override fun getDayForGroup(groupId: Int, date: LocalDate, version: Long) = flow {
        val `class` = groupRepository.getGroupById(groupId)!!
        val school = `class`.school

        lessonRepository.getLessonsForGroup(`class`.groupId, date, version).distinctUntilChanged()
            .collect { lessons ->
                val bookings = roomRepository.getRoomBookings(date)
                emit(
                    build(
                        school,
                        lessons,
                        date,
                        planDao.getPlanByDate(school.id, date)?.planData?.info,
                        bookings
                    )
                )
            }
    }

    override fun getDayForRoom(roomId: Int, date: LocalDate, version: Long) = flow {
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
                        planDao.getPlanByDate(school.id, date)?.planData?.info,
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
        val dayType = holidayRepository.getDayType(school.id, date)
        val lessonsWithBookings = lessons?.map { lesson ->
            val booking = bookings.firstOrNull { roomBooking ->
                roomBooking.bookedBy?.group == lesson.`class` &&
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
                schoolId = plan.school.id,
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
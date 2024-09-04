package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbTimetable
import es.jvbabi.vplanplus.data.source.database.dao.LessonTimeDao
import es.jvbabi.vplanplus.data.source.database.dao.TimetableDao
import es.jvbabi.vplanplus.data.source.database.dao.WeekDao
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.model.WeekType
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.util.DateUtils.withDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

class TimetableRepositoryImpl(
    private val timetableDao: TimetableDao,
    private val weekDao: WeekDao,
    private val lessonTimeDao: LessonTimeDao
) : TimetableRepository {
    override fun insertTimetableLesson(
        group: Group,
        dayOfWeek: DayOfWeek,
        week: Week,
        lessonNumber: Int,
        subject: String,
        rooms: List<Room>,
        teachers: List<Teacher>
    ) {
        val id = UUID.randomUUID()
        timetableDao.upsertTimetable(
            DbTimetable(
                id = id,
                lessonNumber = lessonNumber,
                classId = group.groupId,
                subject = subject,
                dayOfWeek = dayOfWeek.value,
                weekId = week.id,
                weekTypeId = week.type.id
            )
        )
        rooms.forEach { timetableDao.upsertTimetableRoomCrossover(id, it.roomId) }
        teachers.forEach { timetableDao.upsertLessonTeacherCrossover(id, it.teacherId) }
    }

    override fun insertTimetableLesson(group: Group, dayOfWeek: DayOfWeek, weekType: WeekType?, lessonNumber: Int, subject: String, rooms: List<Room>, teachers: List<Teacher>) {
        val id = UUID.randomUUID()
        timetableDao.upsertTimetable(
            DbTimetable(
                id = id,
                lessonNumber = lessonNumber,
                classId = group.groupId,
                subject = subject,
                dayOfWeek = dayOfWeek.value,
                weekTypeId = weekType?.id,
                weekId = null
            )
        )
        rooms.forEach { timetableDao.upsertTimetableRoomCrossover(id, it.roomId) }
        teachers.forEach { timetableDao.upsertLessonTeacherCrossover(id, it.teacherId) }
    }

    override fun clearTimetableForSchool(school: School) {
        timetableDao.clearTimetableForSchool(school.id)
    }

    override suspend fun getTimetableForGroup(group: Group, date: LocalDate): List<Lesson.TimetableLesson> {
        val weeks = weekDao.getWeeksBySchool(group.school.id).map { it.toModel() }
        val week = weeks.firstOrNull { date in it.start..it.end.withDayOfWeek(7) }
        val lessonTimes = lessonTimeDao.getLessonTimesByGroupId(group.groupId)
        if (week == null) {
            val weekTypes = weekDao.getWeekTypesBySchool(group.school.id).map { it.toModel() }
            return timetableDao
                .getTimetableForGroup(group.groupId, null, weekTypes.first().id, date.dayOfWeek.value)
                .map { it.toModel(lessonTimes) }
        }
        return timetableDao
            .getTimetableForGroup(group.groupId, week.id, week.type.id, date.dayOfWeek.value)
            .map { it.toModel(lessonTimes) }
    }
}
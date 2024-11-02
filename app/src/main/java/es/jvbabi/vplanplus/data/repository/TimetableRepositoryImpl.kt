package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbTimetable
import es.jvbabi.vplanplus.data.source.database.crossover.TimetableRoomCrossover
import es.jvbabi.vplanplus.data.source.database.crossover.TimetableTeacherCrossover
import es.jvbabi.vplanplus.data.source.database.dao.GroupDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTimeDao
import es.jvbabi.vplanplus.data.source.database.dao.TimetableDao
import es.jvbabi.vplanplus.data.source.database.dao.WeekDao
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.repository.NewTimetableLesson
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.util.DateUtils.withDayOfWeek
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

class TimetableRepositoryImpl(
    private val timetableDao: TimetableDao,
    private val weekDao: WeekDao,
    private val lessonTimeDao: LessonTimeDao,
    private val groupDao: GroupDao
) : TimetableRepository {
    override suspend fun insertTimetableLesson(
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

    override suspend fun insertTimetableLesson(newTimetableLesson: NewTimetableLesson) {
        val id = UUID.randomUUID()
        timetableDao.upsertTimetable(
            DbTimetable(
                id = id,
                lessonNumber = newTimetableLesson.lessonNumber,
                classId = newTimetableLesson.group.groupId,
                subject = newTimetableLesson.subject,
                dayOfWeek = newTimetableLesson.dayOfWeek.value,
                weekTypeId = newTimetableLesson.weekType?.id,
                weekId = null
            )
        )
        newTimetableLesson.rooms.forEach { timetableDao.upsertTimetableRoomCrossover(id, it.roomId) }
        newTimetableLesson.teachers.forEach { timetableDao.upsertLessonTeacherCrossover(id, it.teacherId) }
    }

    override suspend fun insertTimetableLessons(newTimetableLessons: List<NewTimetableLesson>) {
        val lessonRoomCrossovers = mutableListOf<TimetableRoomCrossover>()
        val lessonTeacherCrossovers = mutableListOf<TimetableTeacherCrossover>()
        val lessons = newTimetableLessons.map {
            val id = UUID.randomUUID()
            val lesson = DbTimetable(
                id = id,
                lessonNumber = it.lessonNumber,
                classId = it.group.groupId,
                subject = it.subject,
                dayOfWeek = it.dayOfWeek.value,
                weekTypeId = it.weekType?.id,
                weekId = null
            )
            lessonRoomCrossovers.addAll(it.rooms.map { room -> TimetableRoomCrossover(id, room.roomId) })
            lessonTeacherCrossovers.addAll(it.teachers.map { teacher -> TimetableTeacherCrossover(id, teacher.teacherId) })
            lesson
        }

        timetableDao.upsertTimetableLessons(lessons)
        timetableDao.upsertTimetableRoomCrossovers(lessonRoomCrossovers)
        timetableDao.upsertLessonTeacherCrossovers(lessonTeacherCrossovers)

    }

    override suspend fun deleteFromTimetableById(ids: List<UUID>) {
        timetableDao.deleteFromTimetableById(ids)
    }

    override suspend fun clearTimetableForSchool(school: School) {
        timetableDao.clearTimetableForSchool(school.id)
    }

    override suspend fun getTimetableForGroup(group: Group, date: LocalDate): List<Lesson.TimetableLesson> {
        val weeks = weekDao.getWeeksBySchool(group.school.id).map { it.toModel() }
        val week = weeks.firstOrNull { date in it.start..it.end.withDayOfWeek(7) }
        val lessonTimes = lessonTimeDao.getLessonTimesByGroupId(group.groupId)
        if (week == null) {
            return timetableDao
                .getTimetableForGroup(group.groupId, null, null, date.dayOfWeek.value)
                .map { it.toModel(lessonTimes) }
        }
        return timetableDao
            .getTimetableForGroup(group.groupId, week.id, week.type.id, date.dayOfWeek.value)
            .map { it.toModel(lessonTimes) }
    }

    override suspend fun getWeekTimetableForSchool(school: School, week: Week?): List<Lesson.TimetableLesson> {
        val groupsWithLessonTimes = groupDao.getGroupsBySchoolId(school.id).first().associate { it.toModel() to lessonTimeDao.getLessonTimesByGroupId(it.group.id) }
        if (week == null) {
            return timetableDao
                .getWeekTimetableForSchool(school.id, null, null)
                .map { lesson -> lesson.toModel(groupsWithLessonTimes.filter { it.key.groupId == lesson.group.group.id }.values.first()) }
        }
        return timetableDao
            .getWeekTimetableForSchool(school.id, week.id, week.type.id)
            .map { lesson -> lesson.toModel(groupsWithLessonTimes.filter { it.key.groupId == lesson.group.group.id }.values.first()) }
    }

    override suspend fun clearCache() {
        weekDao.deleteAllWeeks()
        weekDao.deleteAllWeekTypes()
        timetableDao.deleteAllTimetable()
    }
}
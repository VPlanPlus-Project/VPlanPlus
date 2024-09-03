package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbTimetable
import es.jvbabi.vplanplus.data.source.database.dao.TimetableDao
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.model.WeekType
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import java.time.DayOfWeek
import java.util.UUID

class TimetableRepositoryImpl(
    private val timetableDao: TimetableDao
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
}
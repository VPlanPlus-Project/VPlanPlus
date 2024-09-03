package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbTimetable
import es.jvbabi.vplanplus.data.source.database.dao.TimetableDao
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import java.time.LocalDate
import java.util.UUID

class TimetableRepositoryImpl(
    private val timetableDao: TimetableDao
) : TimetableRepository {
    override fun insertTimetableLesson(group: Group, date: LocalDate, lessonNumber: Int, subject: String, rooms: List<Room>, teachers: List<Teacher>) {
        val id = UUID.randomUUID()
        timetableDao.upsertTimetable(
            DbTimetable(
                id = id,
                lessonNumber = lessonNumber,
                classId = group.groupId,
                subject = subject,
                week = date.atStartOfWeek(),
                dayOfWeek = date.dayOfWeek.value
            )
        )
        rooms.forEach { timetableDao.upsertTimetableRoomCrossover(id, it.roomId) }
        teachers.forEach { timetableDao.upsertLessonTeacherCrossover(id, it.teacherId) }
    }

    override fun clearTimetableForSchool(school: School) {
        timetableDao.clearTimetableForSchool(school.id)
    }
}
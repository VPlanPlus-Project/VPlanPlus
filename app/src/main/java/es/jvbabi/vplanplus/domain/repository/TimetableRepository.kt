package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.model.WeekType
import java.time.DayOfWeek
import java.time.LocalDate

interface TimetableRepository {
    fun insertTimetableLesson(
        group: Group,
        dayOfWeek: DayOfWeek,
        week: Week,
        lessonNumber: Int,
        subject: String,
        rooms: List<Room>,
        teachers: List<Teacher>
    )

    fun insertTimetableLesson(
        group: Group,
        dayOfWeek: DayOfWeek,
        weekType: WeekType?,
        lessonNumber: Int,
        subject: String,
        rooms: List<Room>,
        teachers: List<Teacher>
    )

    fun clearTimetableForSchool(school: School)

    suspend fun getTimetableForGroup(group: Group, date: LocalDate): List<Lesson.TimetableLesson>
}
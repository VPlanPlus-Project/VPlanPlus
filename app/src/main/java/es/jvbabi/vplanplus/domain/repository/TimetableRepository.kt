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
import java.util.UUID

interface TimetableRepository {
    suspend fun insertTimetableLesson(
        group: Group,
        dayOfWeek: DayOfWeek,
        week: Week,
        lessonNumber: Int,
        subject: String,
        rooms: List<Room>,
        teachers: List<Teacher>
    )

    suspend fun insertTimetableLesson(newTimetableLesson: NewTimetableLesson)
    suspend fun insertTimetableLessons(newTimetableLessons: List<NewTimetableLesson>)
    suspend fun deleteFromTimetableById(ids: List<UUID>)

    suspend fun clearTimetableForSchool(school: School)

    suspend fun getTimetableForGroup(group: Group, date: LocalDate): List<Lesson.TimetableLesson>
    suspend fun getWeekTimetableForSchool(school: School, week: Week?): List<Lesson.TimetableLesson>

    suspend fun clearCache()
}

data class NewTimetableLesson(
    val group: Group,
    val dayOfWeek: DayOfWeek,
    val week: Week?,
    val weekType: WeekType?,
    val lessonNumber: Int,
    val subject: String,
    val rooms: List<Room>,
    val teachers: List<Teacher>
)
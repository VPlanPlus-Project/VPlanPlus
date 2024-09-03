package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import java.time.LocalDate

interface TimetableRepository {
    fun insertTimetableLesson(
        group: Group,
        date: LocalDate,
        lessonNumber: Int,
        subject: String,
        rooms: List<Room>,
        teachers: List<Teacher>
    )

    fun clearTimetableForSchool(school: School)
}
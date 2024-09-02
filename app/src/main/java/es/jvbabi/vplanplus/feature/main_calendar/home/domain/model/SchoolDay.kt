package es.jvbabi.vplanplus.feature.main_calendar.home.domain.model

import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import java.time.LocalDate

data class SchoolDay(
    val date: LocalDate,
    val info: String? = null,
    val lessons: List<Lesson>,
    val homework: List<PersonalizedHomework> = emptyList(),
    val grades: List<Grade> = emptyList()
) {
    constructor(date: LocalDate) : this(date, null, emptyList())
}
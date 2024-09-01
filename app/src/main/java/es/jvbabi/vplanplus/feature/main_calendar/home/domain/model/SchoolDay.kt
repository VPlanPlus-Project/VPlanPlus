package es.jvbabi.vplanplus.feature.main_calendar.home.domain.model

import es.jvbabi.vplanplus.domain.model.Lesson
import java.time.LocalDate

data class SchoolDay(
    val date: LocalDate,
    val info: String? = null,
    val lessons: List<Lesson>
) {
    constructor(date: LocalDate) : this(date, null, emptyList())
}
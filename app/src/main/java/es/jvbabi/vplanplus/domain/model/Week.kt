package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate

data class Week(
    val id: Int,
    val weekNumber: Int,
    val school: School,
    val start: LocalDate,
    val end: LocalDate,
    val type: WeekType
)
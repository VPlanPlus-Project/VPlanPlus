package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Plan(
    val school: School,
    val date: LocalDate,
    val createAt: LocalDateTime,
    val info: String?,
    val version: Long
)

package es.jvbabi.vplanplus.domain.model

import java.time.ZonedDateTime

data class Plan(
    val school: School,
    val date: ZonedDateTime,
    val createAt: ZonedDateTime,
    val info: String?,
    val version: Long
)

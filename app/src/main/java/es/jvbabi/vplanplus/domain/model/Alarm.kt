package es.jvbabi.vplanplus.domain.model

import java.time.ZonedDateTime

data class Alarm(
    val id: Int,
    val time: ZonedDateTime,
    val tags: List<String>,
    val data: String
)
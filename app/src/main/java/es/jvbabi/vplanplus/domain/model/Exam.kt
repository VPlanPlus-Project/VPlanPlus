package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate
import java.time.ZonedDateTime

data class Exam(
    val id: Int,
    val subject: DefaultLesson?,
    val date: LocalDate,
    val title: String,
    val description: String,
    val type: String,
    val createdBy: VppId?,
    val group: Group,
    val createdAt: ZonedDateTime
)
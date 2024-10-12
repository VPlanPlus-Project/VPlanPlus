package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate
import java.time.ZonedDateTime

data class Exam(
    val id: Int,
    val subject: DefaultLesson?,
    val date: LocalDate,
    val title: String,
    val description: String?,
    val type: ExamType,
    val createdBy: VppId?,
    val group: Group,
    val createdAt: ZonedDateTime
)

enum class ExamType {
    SHORT_TEST, PROJECT, CLASS_TEST, ORAL, OTHER
}
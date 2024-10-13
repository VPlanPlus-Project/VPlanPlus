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
    val createdAt: ZonedDateTime,
    val remindDaysBefore: Set<Int>
)

sealed class ExamType(
    val code: String,
    val remindDaysBefore: Set<Int> = setOf(1, 2)
) {
    data object ShortTest : ExamType(code = "SHORT_TEST")
    data object Project : ExamType(code = "PROJECT", remindDaysBefore = setOf(7, 5, 3, 1))
    data object ClassTest : ExamType(code = "CLASS_TEST", remindDaysBefore = setOf(4, 2, 1))
    data object Oral : ExamType(code = "ORAL")
    data object Other : ExamType(code = "OTHER")

    companion object {
        val values = listOf(ShortTest, Project, ClassTest, Oral, Other)
        fun of(type: String): ExamType {
            return values.firstOrNull { it.code == type } ?: Other
        }
    }
}
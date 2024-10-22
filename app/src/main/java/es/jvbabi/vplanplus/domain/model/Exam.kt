package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate
import java.time.ZonedDateTime

data class Exam(
    val id: Int,
    val subject: DefaultLesson?,
    val date: LocalDate,
    val title: String,
    val description: String?,
    val type: ExamCategory,
    val createdBy: VppId?,
    val group: Group,
    val createdAt: ZonedDateTime,
    val remindDaysBefore: Set<Int>
)

sealed class ExamCategory(
    val code: String,
    val remindDaysBefore: Set<Int> = setOf(1, 2)
) {
    data object ShortTest : ExamCategory(code = "SHORT_TEST")
    data object Project : ExamCategory(code = "PROJECT", remindDaysBefore = setOf(7, 5, 3, 1))
    data object ClassTest : ExamCategory(code = "CLASS_TEST", remindDaysBefore = setOf(4, 2, 1))
    data object Oral : ExamCategory(code = "ORAL")
    data object Other : ExamCategory(code = "OTHER")

    companion object {
        val values = listOf(ShortTest, Project, ClassTest, Oral, Other)
        fun of(type: String): ExamCategory {
            return values.firstOrNull { it.code == type } ?: Other
        }
    }
}
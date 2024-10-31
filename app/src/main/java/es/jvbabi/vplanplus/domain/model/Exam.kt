package es.jvbabi.vplanplus.domain.model

import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class Exam(
    val id: Int,
    val subject: DefaultLesson?,
    val date: LocalDate,
    val title: String,
    val description: String?,
    val type: ExamCategory,
    val group: Group,
    val createdAt: ZonedDateTime,
    val remindDaysBefore: Set<Int>
) {
    class Local(
        id: Int,
        subject: DefaultLesson?,
        date: LocalDate,
        title: String,
        description: String?,
        type: ExamCategory,
        group: Group,
        createdAt: ZonedDateTime,
        remindDaysBefore: Set<Int>
    ) : Exam(id, subject, date, title, description, type, group, createdAt, remindDaysBefore)

    class Cloud(
        id: Int,
        subject: DefaultLesson?,
        date: LocalDate,
        title: String,
        description: String?,
        type: ExamCategory,
        val createdBy: VppId,
        val isPublic: Boolean,
        group: Group,
        createdAt: ZonedDateTime,
        remindDaysBefore: Set<Int>
    ): Exam(id, subject, date, title, description, type, group, createdAt, remindDaysBefore)

    fun copy(
        id: Int = this.id,
        subject: DefaultLesson? = this.subject,
        date: LocalDate = this.date,
        title: String = this.title,
        description: String? = this.description,
        type: ExamCategory = this.type,
        group: Group = this.group,
        createdAt: ZonedDateTime = this.createdAt,
        remindDaysBefore: Set<Int> = this.remindDaysBefore
    ) = when (this) {
        is Local -> Local(id, subject, date, title, description, type, group, createdAt, remindDaysBefore)
        is Cloud -> Cloud(id, subject, date, title, description, type, createdBy, isPublic, group, createdAt, remindDaysBefore)
    }

    @OptIn(ExperimentalContracts::class)
    fun getAuthor(): VppId? {
        contract {
            returns(null) implies (this@Exam is Local)
            returnsNotNull() implies (this@Exam is Cloud)
        }
        return when (this) {
            is Local -> null
            is Cloud -> createdBy
        }
    }

    fun equalsContent(other: Exam): Boolean {
        return this.id == other.id &&
                this.subject == other.subject &&
                this.date == other.date &&
                this.type == other.type &&
                this.title == other.title &&
                this.description == other.description &&
                this.createdAt == other.createdAt
    }
}

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
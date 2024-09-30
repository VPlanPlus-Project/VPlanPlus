package es.jvbabi.vplanplus.feature.main_grades.view.domain.model

import es.jvbabi.vplanplus.domain.model.VppId
import java.time.LocalDate

data class Grade(
    val id: Long,
    val givenAt: LocalDate,
    val givenBy: Teacher,
    val value: Float,
    val modifier: GradeModifier,
    val subject: Subject,
    val vppId: VppId,
    val type: String,
    val comment: String,
    val interval: Interval,
    val year: Year
) {
    val actualValue: Int?
        get() = if (!interval.isSek2 && value == 0f) null else value.toInt()



    override fun equals(other: Any?): Boolean {
        if (other is Grade) return id == other.id
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + givenAt.hashCode()
        result = 31 * result + givenBy.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + modifier.hashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + vppId.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + interval.hashCode()
        result = 31 * result + year.hashCode()
        return result
    }
}

enum class GradeModifier(val char: String) {
    PLUS("+"), NEUTRAL(""), MINUS("−")
}

data class DownloadedGrade(
    val id: Long,
    val givenAt: LocalDate,
    val givenBy: Teacher,
    val value: Float,
    val modifier: GradeModifier,
    val subject: Subject,
    val vppId: VppId,
    val type: String,
    val comment: String,
    val intervalId: Int
) {
    fun toGrade(year: Year, interval: Interval): Grade {
        return Grade(
            id = id,
            givenAt = givenAt,
            givenBy = givenBy,
            value = value,
            modifier = modifier,
            subject = subject,
            vppId = vppId,
            type = type,
            comment = comment,
            interval = interval,
            year = year
        )
    }
}
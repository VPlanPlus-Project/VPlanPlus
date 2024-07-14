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
        get() = if (interval.type != "Sek II" && value == 0f) null else value.toInt()
}

enum class GradeModifier {
    PLUS, NEUTRAL, MINUS
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
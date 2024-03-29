package es.jvbabi.vplanplus.feature.main_grades.domain.model

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
    val comment: String
)

enum class GradeModifier(val symbol: String) {
    PLUS("+"), NEUTRAL(""), MINUS("-")
}
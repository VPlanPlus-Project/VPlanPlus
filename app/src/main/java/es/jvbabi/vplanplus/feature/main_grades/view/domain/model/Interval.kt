package es.jvbabi.vplanplus.feature.main_grades.view.domain.model

import java.time.LocalDate

data class Interval(
    val id: Long,
    val name: String,
    val type: String,
    val from: LocalDate,
    val to: LocalDate,
    val includedIntervalId: Long?,
    val yearId: Long,
) {
    val isSek2: Boolean
        get() = type == "Sek II" || type.matches(Regex("JG( )?\\d{2}"))
}

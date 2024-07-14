package es.jvbabi.vplanplus.feature.main_grades.view.domain.model

import java.time.LocalDate

data class Year(
    val id: Long,
    val name: String,
    val from: LocalDate,
    val to: LocalDate
)
package es.jvbabi.vplanplus.feature.main_grades.data.model

import androidx.room.Entity
import java.time.LocalDate

@Entity(
    tableName = "bs_years",
    primaryKeys = ["id"]
)
data class DbYear(
    val id: Long,
    val name: String,
    val from: LocalDate,
    val to: LocalDate
)
package es.jvbabi.vplanplus.feature.main_grades.view.data.model

import androidx.room.Entity
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Year
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
) {
    fun toModel(): Year {
        return Year(
            id = id,
            name = name,
            from = from,
            to = to
        )
    }
}
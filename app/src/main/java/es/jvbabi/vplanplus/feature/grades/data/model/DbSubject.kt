package es.jvbabi.vplanplus.feature.grades.data.model

import androidx.room.Entity
import androidx.room.Index
import es.jvbabi.vplanplus.feature.grades.domain.model.Subject

@Entity(
    tableName = "grade_subject",
    indices = [
        Index(value = ["id"], unique = true)
    ],
    primaryKeys = ["id"]
)
data class DbSubject(
    val id: Long,
    val short: String,
    val name: String
) {
    fun toModel(): Subject {
        return Subject(
            id = id,
            short = short,
            name = name
        )
    }
}
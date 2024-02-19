package es.jvbabi.vplanplus.feature.grades.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.feature.grades.domain.model.GradeModifier
import java.time.LocalDate

@Entity(
    tableName = "grade",
    indices = [
        Index(value = ["id"], unique = true)
    ],
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["givenBy"],
            entity = DbTeacher::class
        ),
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["subject"],
            entity = DbSubject::class
        ),
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["vppId"],
            entity = DbVppId::class
        )
    ]
)
data class DbGrade(
    val id: Long,
    val givenAt: LocalDate,
    val givenBy: Long,
    val subject: Long,
    val value: Float,
    val type: String,
    val comment: String,
    val modifier: GradeModifier,
    val vppId: Int
)
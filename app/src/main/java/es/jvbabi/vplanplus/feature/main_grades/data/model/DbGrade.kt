package es.jvbabi.vplanplus.feature.main_grades.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier
import java.time.LocalDate

@Entity(
    tableName = "grade",
    indices = [
        Index(value = ["id"], unique = true)
    ],
    primaryKeys = ["id", "givenBy", "subject", "vppId"],
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["givenBy"],
            entity = DbTeacher::class
        ),
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["subject"],
            entity = DbSubject::class,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["vppId"],
            entity = DbVppId::class,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["interval"],
            entity = DbInterval::class,
            onDelete = ForeignKey.CASCADE
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
    val vppId: Int,
    val interval: Long
)
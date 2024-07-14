package es.jvbabi.vplanplus.feature.main_grades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier
import java.time.LocalDate

@Entity(
    tableName = "grade",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["teacher_id"], name = "index_grade_teacher_ref_id"),
        Index(value = ["vpp_id"]),
        Index(value = ["interval_id"]),
        Index(value = ["subject_id"], name = "index_grade_subject_ref_id")
    ],
    primaryKeys = ["id", "teacher_id", "subject_id", "vpp_id"],
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
            entity = DbTeacher::class
        ),
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            entity = DbSubject::class,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["vpp_id"],
            entity = DbVppId::class,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["interval_id"],
            entity = DbInterval::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DbGrade(
    @ColumnInfo("id") val id: Long,
    @ColumnInfo("given_at") val givenAt: LocalDate,
    @ColumnInfo("teacher_id") val teacherId: Long,
    @ColumnInfo("subject_id") val subject: Long,
    @ColumnInfo("value") val value: Float,
    @ColumnInfo("type") val type: String,
    @ColumnInfo("comment") val comment: String,
    @ColumnInfo("modifier") val modifier: GradeModifier,
    @ColumnInfo("vpp_id") val vppId: Int,
    @ColumnInfo("interval_id") val interval: Long
)
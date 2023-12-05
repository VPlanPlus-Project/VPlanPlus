package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.domain.model.School
import java.util.UUID

@Entity(
    tableName = "teacher",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolTeacherRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["teacherId"],
    indices = [
        Index(value = ["teacherId"], unique = true),
        Index(value = ["schoolTeacherRefId"]),
    ]
)
data class DbTeacher(
    val teacherId: UUID = UUID.randomUUID(),
    val acronym: String,
    val schoolTeacherRefId: Long,
)

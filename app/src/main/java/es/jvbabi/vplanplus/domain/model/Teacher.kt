package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "teacher",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolTeacherRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Teacher(
    @PrimaryKey(autoGenerate = true) val teacherId: Long = 0,
    val acronym: String,
    val schoolTeacherRefId: Long,
)

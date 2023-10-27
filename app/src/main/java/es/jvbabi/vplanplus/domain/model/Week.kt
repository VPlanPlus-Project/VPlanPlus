package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "weeks",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["id"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Week(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val schoolId: Long,
    val week: Int,
    val start: Long,
    val end: Long,
    val type: String
)

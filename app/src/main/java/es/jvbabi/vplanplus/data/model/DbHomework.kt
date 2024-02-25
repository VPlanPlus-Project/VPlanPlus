package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "homework",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = DbVppId::class,
            parentColumns = ["id"],
            childColumns = ["createdBy"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["classes"],
            onDelete = CASCADE
        )
    ]
)
data class DbHomework(
    val id: Int,
    val createdBy: Int?,
    val classes: UUID,
    val createdAt: LocalDate,
    val defaultLessonVpId: Long,
    val until: LocalDate
)
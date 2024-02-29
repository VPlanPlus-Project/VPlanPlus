package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import java.time.ZonedDateTime
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
    val id: Long,
    val createdBy: Int?,
    val classes: UUID,
    @ColumnInfo(defaultValue = "false") val isPublic: Boolean = false,
    val createdAt: ZonedDateTime,
    val defaultLessonVpId: Long,
    val until: ZonedDateTime
)
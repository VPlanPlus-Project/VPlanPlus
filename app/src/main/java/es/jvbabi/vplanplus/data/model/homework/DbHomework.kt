package es.jvbabi.vplanplus.data.model.homework

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
    tableName = "homework",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["created_by"]),
        Index(value = ["group_id"]),
        Index(value = ["profile_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbVppId::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = DbClassProfile::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = CASCADE,
        )
    ]
)
data class DbHomework(
    @ColumnInfo("id") val id: Long,
    @ColumnInfo("created_by") val createdBy: Int?,
    @ColumnInfo("group_id") val groupId: Int,
    @ColumnInfo("is_public", defaultValue = "false") val isPublic: Boolean = false,
    @ColumnInfo("created_at") val createdAt: ZonedDateTime,
    @ColumnInfo("default_lesson_vp_id") val defaultLessonVpId: Int?,
    @ColumnInfo("until") val until: ZonedDateTime,
    @ColumnInfo("profile_id") val owningProfileId: UUID?
)
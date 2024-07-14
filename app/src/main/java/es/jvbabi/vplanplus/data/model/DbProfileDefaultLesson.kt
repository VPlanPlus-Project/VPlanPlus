package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import java.util.UUID

@Entity(
    tableName = "profile_default_lesson",
    primaryKeys = ["profile_id", "vp_id"],
    foreignKeys = [
        ForeignKey(
            entity = DbClassProfile::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profile_id"]),
        Index(value = ["vp_id"])
    ]
)
data class DbProfileDefaultLesson(
    @ColumnInfo("profile_id") val profileId: UUID,
    @ColumnInfo("vp_id") val defaultLessonVpId: Long,
    @ColumnInfo("enabled") val enabled: Boolean
)

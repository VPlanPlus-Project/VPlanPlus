package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "profile_default_lesson",
    primaryKeys = ["profileId", "defaultLessonVpId"],
    foreignKeys = [
        ForeignKey(
            entity = DbProfile::class,
            parentColumns = ["profileId"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId"])
    ]
)
data class DbProfileDefaultLesson(
    val profileId: UUID,
    val defaultLessonVpId: Long,
    val enabled: Boolean
)

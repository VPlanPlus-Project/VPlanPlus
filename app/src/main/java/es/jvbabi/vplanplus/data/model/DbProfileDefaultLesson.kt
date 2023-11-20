package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "profile_default_lesson",
    primaryKeys = ["profileId", "defaultLessonVpId"],
    foreignKeys = [
        ForeignKey(
            entity = DbProfile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbProfileDefaultLesson(
    val profileId: Long,
    val defaultLessonVpId: Long,
    val enabled: Boolean
)

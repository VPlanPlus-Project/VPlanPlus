package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "profile",
    primaryKeys = ["profileId"],
    indices = [
        Index(value = ["profileId"], unique = true),
        Index(value = ["referenceId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["referenceId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DbVppId::class,
            parentColumns = ["id"],
            childColumns = ["linkedVppId"],
            onDelete = ForeignKey.SET_NULL,
        )
    ]
)
data class DbProfile(
    val profileId: UUID = UUID.randomUUID(),
    val type: ProfileType,
    val name: String,
    val customName: String,
    val calendarMode: ProfileCalendarType,
    val calendarId: Long?,
    val referenceId: UUID,
    val linkedVppId: Int? = null,
)

enum class ProfileType {
    TEACHER, STUDENT, ROOM
}

enum class ProfileCalendarType {
    DAY, LESSON, NONE
}
package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "profile",
    primaryKeys = ["profileId"],
    indices = [
        Index(value = ["profileId"], unique = true),
    ]
)
data class DbProfile(
    val profileId: UUID = UUID.randomUUID(),
    val type: ProfileType,
    val name: String,
    val customName: String,
    val calendarMode: ProfileCalendarType,
    val calendarId: Long?,
    val referenceId: UUID, // can be class id, teacher id or room id
)

enum class ProfileType {
    TEACHER, STUDENT, ROOM
}

enum class ProfileCalendarType {
    DAY, LESSON, NONE
}
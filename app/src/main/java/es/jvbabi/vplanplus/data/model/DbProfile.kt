package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "profile"
)
data class DbProfile(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val type: ProfileType,
    val name: String,
    val customName: String,
    val calendarMode: ProfileCalendarType,
    val calendarId: Long?,
    val referenceId: Long, // can be class id, teacher id or room id
)

enum class ProfileType {
    TEACHER, STUDENT, ROOM
}

enum class ProfileCalendarType {
    DAY, LESSON, NONE
}
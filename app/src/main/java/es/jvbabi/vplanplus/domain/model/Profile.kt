package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.jvbabi.vplanplus.ui.screens.home.MenuProfile

@Entity(
    tableName = "profile"
)
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val type: ProfileType,
    val name: String,
    val referenceId: Long, // can be class id, teacher id or room id
) {
    fun toMenuProfile(): MenuProfile {
        return MenuProfile(id!!, name)
    }
}

enum class ProfileType {
    TEACHER, STUDENT, ROOM
}
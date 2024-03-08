package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import java.time.ZonedDateTime

@Entity(
    tableName = "messages",
    primaryKeys = ["id"]
)
data class Message(
    val id: String,
    val title: String,
    val content: String,
    val date: ZonedDateTime,
    val isRead: Boolean,
    val importance: Importance,
    val fromVersion: Int,
    val toVersion: Int,
    val schoolId: Long?,
    val notificationSent: Boolean = false
)

enum class Importance {
    LOW, MEDIUM, HIGH
}

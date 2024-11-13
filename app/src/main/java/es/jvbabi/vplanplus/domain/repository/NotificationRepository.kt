package es.jvbabi.vplanplus.domain.repository

import android.content.Context
import androidx.core.app.NotificationCompat
import es.jvbabi.vplanplus.domain.model.Profile

interface NotificationRepository {
    suspend fun sendNotification(
        channelId: String,
        id: Int,
        title: String,
        subtitle: String? = null,
        message: String,
        icon: Int,
        onClickTask: NotificationOnClickTask? = null,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        actions: List<NotificationAction> = emptyList()
    )

    fun createChannel(channelId: String, name: String, description: String, importance: Int)

    fun deleteChannel(channelId: String)
    fun createSystemChannels(context: Context)
    fun createProfileChannels(context: Context, profiles: List<Profile>)
    fun dismissNotification(id: Int)

    fun deleteAllChannels()

    companion object {
        const val CHANNEL_ID_SYSTEM = "system"
        const val CHANNEL_ID_GRADES = "grades"
        const val CHANNEL_ID_ROOM_BOOKINGS = "room_bookings"
        const val CHANNEL_ID_NEWS = "news"
        const val CHANNEL_ID_SYNC = "sync"
        const val CHANNEL_ID_HOMEWORK = "homework"
        const val CHANNEL_ID_DAILY = "daily"
        const val CHANNEL_ID_ASSESSMENTS = "assessments"

        const val CHANNEL_DEFAULT_NOTIFICATION_ID_HOMEWORK = 7000
        const val CHANNEL_DEFAULT_NOTIFICATION_ID_NEW_HOMEWORK = CHANNEL_DEFAULT_NOTIFICATION_ID_HOMEWORK + 1
        const val CHANNEL_HOMEWORK_REMINDER_NOTIFICATION_ID = 8000
        const val CHANNEL_SYSTEM_NOTIFICATION_ID = 100000
        const val ID_GRADE = 9000
        const val ID_GRADE_NEW = ID_GRADE + 1
        const val CHANNEL_DEFAULT_NOTIFICATION_ID_VPP_AUTH = 11000
        const val CHANNEL_DEFAULT_DAILY_ID = 12000
        const val CHANNEL_DEFAULT_ASSESSMENTS_ID = 13000
    }
}

data class NotificationAction(
    val title: String,
    val task: NotificationOnClickTask
)

interface NotificationOnClickTask

class OpenScreenTask(val destination: String) : NotificationOnClickTask
class OpenLinkTask(val url: String) : NotificationOnClickTask
class DoActionTask(val tag: String, val payload: String? = null): NotificationOnClickTask
class BroadcastIntentTask(val tag: String, val payload: String? = null): NotificationOnClickTask
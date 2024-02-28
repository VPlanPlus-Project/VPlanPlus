package es.jvbabi.vplanplus.domain.repository

import android.app.PendingIntent
import android.content.Context
import es.jvbabi.vplanplus.domain.model.Profile

interface NotificationRepository {
    suspend fun sendNotification(
        channelId: String,
        id: Int,
        title: String,
        message: String,
        icon: Int,
        pendingIntent: PendingIntent?
    )

    fun createChannel(channelId: String, name: String, description: String, importance: Int)

    fun deleteChannel(channelId: String)
    fun createSystemChannels(context: Context)
    fun createProfileChannels(context: Context, profiles: List<Profile>)

    companion object {
        const val CHANNEL_ID_GRADES = "grades"
        const val CHANNEL_ID_ROOM_BOOKINGS = "room_bookings"
        const val CHANNEL_ID_NEWS = "news"
        const val CHANNEL_ID_SYNC = "sync"
        const val CHANNEL_ID_HOMEWORK = "homework"
    }
}
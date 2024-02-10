package es.jvbabi.vplanplus.domain.repository

import android.app.PendingIntent

interface NotificationRepository {
    suspend fun sendNotification(
        channelId: String,
        id: Int,
        title: String,
        message: String,
        icon: Int,
        pendingIntent: PendingIntent?
    )

    fun deleteChannel(channelId: String)
}
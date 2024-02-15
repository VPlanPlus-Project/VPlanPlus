package es.jvbabi.vplanplus.shared.data

import android.app.PendingIntent
import es.jvbabi.vplanplus.domain.repository.NotificationRepository

class FakeNotificationRepository : NotificationRepository {
    override suspend fun sendNotification(
        channelId: String,
        id: Int,
        title: String,
        message: String,
        icon: Int,
        pendingIntent: PendingIntent?
    ) {}

    override fun createChannel(
        channelId: String,
        name: String,
        description: String,
        importance: Int
    ) {}

    override fun deleteChannel(channelId: String) {}
}
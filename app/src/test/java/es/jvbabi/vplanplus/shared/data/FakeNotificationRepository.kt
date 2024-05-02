package es.jvbabi.vplanplus.shared.data

import android.app.PendingIntent
import android.content.Context
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.NotificationAction
import es.jvbabi.vplanplus.domain.repository.NotificationRepository

class FakeNotificationRepository : NotificationRepository {
    override suspend fun sendNotification(channelId: String, id: Int, title: String, message: String, icon: Int, pendingIntent: PendingIntent?, priority: Int, actions: List<NotificationAction>) {
        TODO("Not yet implemented")
    }

    override fun createChannel(
        channelId: String,
        name: String,
        description: String,
        importance: Int
    ) {}

    override fun deleteChannel(channelId: String) {}
    override fun createSystemChannels(context: Context) {
        TODO("Not yet implemented")
    }

    override fun createProfileChannels(context: Context, profiles: List<Profile>) {
        TODO("Not yet implemented")
    }

    override fun dismissNotification(id: Int) {
        TODO("Not yet implemented")
    }
}
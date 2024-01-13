package es.jvbabi.vplanplus.android.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Profile

object Notification {

    fun createChannel(context: Context, id: String, name: String, description: String, importance: Int) {
        val channel = NotificationChannel(
            id,
            name,
            importance
        ).apply {
            this.description = description
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun createChannels(context: Context, profiles: List<Profile>) {
        createChannel(
            context,
            "SYNC",
            context.getString(R.string.notification_syncName),
            context.getString(R.string.notification_syncDescription),
            NotificationManager.IMPORTANCE_LOW
        )

        createChannel(
            context,
            "NEWS",
            context.getString(R.string.notification_newsName),
            context.getString(R.string.notification_newsDescription),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        profiles.forEach {
            createChannel(
                context,
                "PROFILE_${it.id.toString().lowercase()}",
                context.getString(R.string.notification_profileName, it.displayName),
                context.getString(R.string.notification_profileDescription),
                NotificationManager.IMPORTANCE_HIGH
            )
        }
    }

    fun deleteChannel(context: Context, id: String) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(id)
    }
}
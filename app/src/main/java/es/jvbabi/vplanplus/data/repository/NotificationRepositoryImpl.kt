package es.jvbabi.vplanplus.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_GRADES
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_NEWS
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_ROOM_BOOKINGS
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_SYNC
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository

class NotificationRepositoryImpl(
    private val appContext: Context,
    private val logRepository: LogRecordRepository
) : NotificationRepository {
    override suspend fun sendNotification(
        channelId: String,
        id: Int,
        title: String,
        message: String,
        icon: Int,
        pendingIntent: PendingIntent?
    ) {
        logRepository.log("Notification", "Sending $id to $channelId: $title")

        val builder = NotificationCompat.Builder(appContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setSmallIcon(icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }

    override fun createChannel(
        channelId: String,
        name: String,
        description: String,
        importance: Int
    ) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                name,
                importance
            ).apply {
                this.description = description
            }
        )
    }

    override fun deleteChannel(channelId: String) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(channelId)
    }

    override fun createSystemChannels(context: Context) {
        createChannel(
            CHANNEL_ID_GRADES,
            context.getString(R.string.grades_notificationTitle),
            context.getString(R.string.grades_notificationDescription),
            NotificationManager.IMPORTANCE_HIGH
        )
        createChannel(
            CHANNEL_ID_NEWS,
            context.getString(R.string.notification_newsName),
            context.getString(R.string.notification_newsDescription),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        createChannel(
            CHANNEL_ID_SYNC,
            context.getString(R.string.notification_syncName),
            context.getString(R.string.notification_syncDescription),
            NotificationManager.IMPORTANCE_NONE
        )
        createChannel(
            CHANNEL_ID_ROOM_BOOKINGS,
            context.getString(R.string.notification_roomBookingsName),
            context.getString(R.string.notification_roomBookingsDescription),
            NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    override fun createProfileChannels(context: Context, profiles: List<Profile>) {
        profiles.forEach {
            createChannel(
                "PROFILE_${it.id.toString().lowercase()}",
                context.getString(R.string.notification_profileName, it.displayName),
                context.getString(R.string.notification_profileDescription),
                NotificationManager.IMPORTANCE_HIGH
            )
        }
    }
}
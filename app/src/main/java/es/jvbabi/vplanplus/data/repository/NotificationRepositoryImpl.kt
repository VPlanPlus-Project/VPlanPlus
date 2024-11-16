package es.jvbabi.vplanplus.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.android.receiver.DailyRemindLaterReceiver
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.BroadcastIntentTask
import es.jvbabi.vplanplus.domain.repository.DoActionTask
import es.jvbabi.vplanplus.domain.repository.NotificationAction
import es.jvbabi.vplanplus.domain.repository.NotificationOnClickTask
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_ASSESSMENTS
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_DAILY
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_GRADES
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_HOMEWORK
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_NEWS
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_ROOM_BOOKINGS
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_SYNC
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_SYSTEM
import es.jvbabi.vplanplus.domain.repository.OpenLinkTask
import es.jvbabi.vplanplus.domain.repository.OpenScreenTask
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository

class NotificationRepositoryImpl(
    private val appContext: Context,
    private val logRepository: LogRecordRepository
) : NotificationRepository {
    override suspend fun sendNotification(
        channelId: String,
        id: Int,
        title: String,
        subtitle: String?,
        message: String,
        icon: Int,
        onClickTask: NotificationOnClickTask?,
        priority: Int,
        actions: List<NotificationAction>
    ) {
        logRepository.log("Notification", "Sending $id to $channelId: $title")

        val taskToIntent: (task: NotificationOnClickTask) -> PendingIntent? = { task ->
            when (task) {
                is OpenScreenTask -> {
                    val intent = Intent(appContext, MainActivity::class.java)
                        .putExtra("screen", task.destination)

                    PendingIntent.getActivity(appContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                }
                is DoActionTask -> {
                    val intent = Intent(appContext, MainActivity::class.java)
                        .putExtra("tag", task.tag)
                        .apply { if (task.payload != null) putExtra("payload", task.payload) }

                    PendingIntent.getActivity(appContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                }
                is OpenLinkTask -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                        .setData(task.url.toUri())

                    PendingIntent.getActivity(appContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                }
                is BroadcastIntentTask -> {
                    val broadcastClass = when (task.tag) {
                        DailyRemindLaterReceiver.TAG -> DailyRemindLaterReceiver::class.java
                        else -> throw IllegalArgumentException("Unknown tag ${task.tag}")
                    }
                    val intent = Intent(appContext, broadcastClass)
                        .putExtra("tag", task.tag)
                        .putExtra("notificationId", id)
                        .let { if (task.payload != null) it.putExtra("payload", task.payload) else it }
                    PendingIntent.getBroadcast(
                        appContext,
                        id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
                else -> null
            }
        }

        val builder = NotificationCompat.Builder(appContext, channelId)
            .setContentTitle(title)
            .setSubText(subtitle)
            .setContentText(message)
            .setPriority(priority)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setSmallIcon(icon)
            .setContentIntent(onClickTask?.let { taskToIntent(it) })
            .setAutoCancel(true)

        actions.forEach {
            builder.addAction(0, it.title, taskToIntent(it.task))
        }

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
        createChannel(
            CHANNEL_ID_HOMEWORK,
            context.getString(R.string.notification_homeworkName),
            context.getString(R.string.notification_homeworkDescription),
            NotificationManager.IMPORTANCE_HIGH
        )
        createChannel(
            CHANNEL_ID_SYSTEM,
            context.getString(R.string.notification_systemName),
            context.getString(R.string.notification_systemDescription),
            NotificationManager.IMPORTANCE_HIGH
        )
        createChannel(
            CHANNEL_ID_DAILY,
            context.getString(R.string.notification_dailyName),
            context.getString(R.string.notification_dailyDescription),
            NotificationManager.IMPORTANCE_HIGH
        )
        createChannel(
            CHANNEL_ID_ASSESSMENTS,
            context.getString(R.string.notification_assessmentsName),
            context.getString(R.string.notification_assessmentsDescription),
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

    override fun dismissNotification(id: Int) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    override fun deleteAllChannels() {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notificationChannels.forEach {
            notificationManager.deleteNotificationChannel(it.id)
        }
    }
}

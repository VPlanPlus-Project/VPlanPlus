package es.jvbabi.vplanplus.data.repository

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlin.system.exitProcess

class SystemRepositoryImpl(
    private val context: Context
) : SystemRepository {
    override fun isAppInForeground(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
    }

    override fun closeApp() {
        exitProcess(0)
    }

    override fun canSendNotifications() = flow {
        while (true) {
            emit(NotificationManagerCompat.from(context).areNotificationsEnabled())
            kotlinx.coroutines.delay(100)
        }
    }.distinctUntilChanged()

    override fun canSendNotifications(channelId: String) = flow {
        while (true) {
            emit(NotificationManagerCompat.from(context).getNotificationChannel(channelId)?.importance != NotificationManager.IMPORTANCE_NONE)
            kotlinx.coroutines.delay(100)
        }
    }.distinctUntilChanged()
}
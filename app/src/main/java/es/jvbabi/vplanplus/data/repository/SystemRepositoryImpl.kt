package es.jvbabi.vplanplus.data.repository

import android.app.ActivityManager
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import kotlin.system.exitProcess

class SystemRepositoryImpl : SystemRepository {
    override fun isAppInForeground(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
    }

    override fun closeApp() {
        exitProcess(0)
    }
}
package es.jvbabi.vplanplus.util

import android.app.ActivityManager

object App {

    fun isAppInForeground(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
    }
}
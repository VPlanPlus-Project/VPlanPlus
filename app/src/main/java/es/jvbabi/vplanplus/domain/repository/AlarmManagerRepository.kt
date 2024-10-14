package es.jvbabi.vplanplus.domain.repository

interface AlarmManagerRepository {
    fun setAlarm(epochSecond: Long, tag: String, data: String)
    fun cancelAlarm(data: String)
    fun canRequestAlarm(): Boolean

    companion object {
        const val TAG_HOMEWORK_NOTIFICATION = "homework_notification"
        const val TAG_NDP_REMINDER = "ndp_reminder"
    }
}
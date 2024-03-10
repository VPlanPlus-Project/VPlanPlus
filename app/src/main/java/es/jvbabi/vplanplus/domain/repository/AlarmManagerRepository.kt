package es.jvbabi.vplanplus.domain.repository

interface AlarmManagerRepository {
    fun setAlarm(epochSecond: Long, tag: String, data: String)
    fun cancelAlarm(data: String)

    companion object {
        const val TAG_HOMEWORK_NOTIFICATION = "homework_notification"
    }
}
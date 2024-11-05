package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.ClassProfile
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface KeyValueRepository {

    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)
    suspend fun getOrDefault(key: String, defaultValue: String): String
    suspend fun delete(key: String)
    fun getFlow(key: String): Flow<String?>
    fun getFlowOrDefault(key: String, defaultValue: String): Flow<String>
}

object Keys {
    const val APP_DEVELOPER_MODE = "APP_DEVELOPER_MODE"
    const val ACTIVE_PROFILE = "ACTIVE_PROFILE"

    const val SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE =
        "SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE"

    const val SETTINGS_SYNC_DAY_DIFFERENCE = "SETTINGS_SYNC_DAY_DIFFERENCE"
    const val SETTINGS_SYNC_DAY_DIFFERENCE_DEFAULT = 5

    const val SETTINGS_SYNC_INTERVAL = "SETTINGS_SYNC_INTERVAL"
    const val SETTINGS_SYNC_INTERVAL_DEFAULT = 15

    const val LESSON_VERSION_NUMBER = "LESSON_VERSION_NUMBER"

    const val LAST_SYNC_TS = "LAST_SYNC_TS"

    const val COLOR = "COLOR"

    const val FCM_TOKEN = "FCM_TOKEN"
    const val FCM_DEBUG_MODE = "FCM_DEBUG_MODE"

    const val GRADES_SHOW_CALCULATION_DISCLAIMER_BANNER = "GRADES_SHOW_CALCULATION_DISCLAIMER_BANNER"

    const val IS_HOMEWORK_UPDATE_RUNNING = "IS_HOMEWORK_UPDATE_RUNNING"

    const val INFO_CLOSED_FOR_DATE = "INFO_CLOSED_FOR_DATE"

    const val GRADES_BIOMETRIC_ENABLED = "GRADES_BIOMETRIC_ENABLED"
    const val SHOW_ENABLE_BIOMETRIC_BANNER = "SHOW_ENABLE_BIOMETRIC_BANNER"

    const val VPPID_SERVER = "VPPID_SERVER"

    const val SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK = "SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK"
    const val SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_DEFAULT = "false"
    const val SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_LATER_SECONDS = "SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_LATER_SECONDS"
    const val SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_LATER_SECONDS_DEFAULT = (30*60).toString()

    const val SETTINGS_PREFERRED_NOTIFICATION_TIME = "SETTINGS_PREFERRED_NOTIFICATION_TIME"
    const val SETTINGS_PREFERRED_NOTIFICATION_TIME_DEFAULT = (60*60*15) + (60*30L) // 15:30

    const val LAST_VERSION_HINTS_VERSION = "LAST_VERSION_HINTS_VERSION"

    const val APP_THEME_MODE = "APP_THEME_MODE"

    const val HIDE_FINISHED_LESSONS = "HIDE_FINISHED_LESSONS"

    const val INVALID_VPP_SESSION = "INVALID_VPP_SESSION"

    const val SHOW_ROOM_BOOKING_DISCLAIMER_BANNER = "SHOW_ROOM_BOOKING_DISCLAIMER_BANNER"
    const val MISSING_VPP_ID_TO_PROFILE_CONNECTION = "MISSING_VPP_ID_TO_PROFILE_CONNECTION"

    const val LAST_KNOWN_APP_VERSION = "LAST_KNOWN_APP_VERSION"

    const val SHOW_TIMETABLE_INFO_BANNER = "SHOW_TIMETABLE_INFO_BANNER"

    private const val DAILY_REMINDER_ENABLED = "DAILY_REMINDER_ENABLED"
    private const val DAILY_REMINDER_TIME = "DAILY_REMINDER_TIME"

    fun isDailyReminderEnabled(profile: ClassProfile) = DAILY_REMINDER_ENABLED + "_" + profile.id
    fun dailyReminderTime(profile: ClassProfile, dayOfWeek: DayOfWeek) = DAILY_REMINDER_TIME + "_" + profile.id + "_" + dayOfWeek.name
}
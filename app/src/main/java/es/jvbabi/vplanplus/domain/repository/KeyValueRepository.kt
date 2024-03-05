package es.jvbabi.vplanplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface KeyValueRepository {

    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)
    suspend fun getOrDefault(key: String, defaultValue: String): String
    suspend fun delete(key: String)
    fun getFlow(key: String): Flow<String?>
    fun getFlowOrDefault(key: String, defaultValue: String): Flow<String>
}

object Keys {
    const val ACTIVE_PROFILE = "ACTIVE_PROFILE"

    const val SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE =
        "SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE"

    const val SETTINGS_SYNC_DAY_DIFFERENCE = "SETTINGS_SYNC_DAY_DIFFERENCE"
    const val SETTINGS_SYNC_DAY_DIFFERENCE_DEFAULT = 5

    const val LESSON_VERSION_NUMBER = "LESSON_VERSION_NUMBER"

    const val LAST_SYNC_TS = "LAST_SYNC_TS"

    const val COLOR = "COLOR"

    const val FCM_TOKEN = "FCM_TOKEN"

    const val SHOW_BS_BANNER = "SHOW_BS_BANNER"

    const val SHOW_VPPID_BANNER_HOMEWORK = "SHOW_VPPID_BANNER_HOMEWORK"

    const val IS_HOMEWORK_UPDATE_RUNNING = "IS_HOMEWORK_UPDATE_RUNNING"

    const val INFO_CLOSED_FOR_DATE = "INFO_CLOSED_FOR_DATE"

    const val GRADES_BIOMETRIC_ENABLED = "GRADES_BIOMETRIC_ENABLED"
    const val SHOW_ENABLE_BIOMETRIC_BANNER = "SHOW_ENABLE_BIOMETRIC_BANNER"
}
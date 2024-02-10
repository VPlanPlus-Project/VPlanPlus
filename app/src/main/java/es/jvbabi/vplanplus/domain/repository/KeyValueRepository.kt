package es.jvbabi.vplanplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface KeyValueRepository {

    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)
    suspend fun getOrDefault(key: String, defaultValue: String): String
    suspend fun delete(key: String)
    fun getFlow(key: String): Flow<String?>
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
}
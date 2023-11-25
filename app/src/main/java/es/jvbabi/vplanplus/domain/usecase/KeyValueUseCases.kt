package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import kotlinx.coroutines.flow.Flow

class KeyValueUseCases(
    private val keyValueRepository: KeyValueRepository
) {
    suspend fun get(key: String): String? {
        return keyValueRepository.get(key = key)
    }

    suspend fun set(key: String, value: String) {
        keyValueRepository.set(key = key, value = value)
    }

    fun getFlow(key: String): Flow<String?> = keyValueRepository.getFlow(key = key)

    suspend fun getOrDefault(key: String, defaultValue: String): String {
        return keyValueRepository.getOrDefault(key = key, defaultValue = defaultValue)
    }
}

object Keys {
    const val ACTIVE_PROFILE = "ACTIVE_PROFILE"

    const val SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE =
        "SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE"

    const val SETTINGS_SYNC_DAY_DIFFERENCE = "SETTINGS_SYNC_DAY_DIFFERENCE"

    const val LESSON_VERSION_NUMBER = "LESSON_VERSION_NUMBER"

    const val LAST_SYNC_TS = "LAST_SYNC_TS"
}
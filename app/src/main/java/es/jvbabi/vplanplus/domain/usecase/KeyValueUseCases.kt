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

    fun getFlow(key: String): Flow<String?> {
        return keyValueRepository.getFlow(key = key)
    }
}

object Keys {
    const val ACTIVE_PROFILE = "ACTIVE_PROFILE"
    const val SYNCING = "SYNCING"

    const val SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE = "SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE"
}
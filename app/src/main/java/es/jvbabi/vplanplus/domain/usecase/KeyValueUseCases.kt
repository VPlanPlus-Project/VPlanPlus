package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class KeyValueUseCases(
    private val keyValueRepository: KeyValueRepository
) {
        suspend fun get(key: String): String? {
            return keyValueRepository.get(key = key)
        }

        suspend fun set(key: String, value: String) {
            keyValueRepository.set(key = key, value = value)
        }
}

enum class Keys {
    ACTIVE_PROFILE
}
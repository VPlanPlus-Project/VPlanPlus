package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeKeyValueRepository : KeyValueRepository {
    private val map = mutableMapOf<String, String>()
    override suspend fun get(key: String): String? {
        return map[key]
    }

    override suspend fun set(key: String, value: String) {
        map[key] = value
    }

    override suspend fun getOrDefault(key: String, defaultValue: String): String {
        return map[key] ?: defaultValue
    }

    override suspend fun delete(key: String) {
        map.remove(key)
    }

    override fun getFlow(key: String): Flow<String?> = flow {
        emit(map[key])
    }

    override fun getFlowOrDefault(key: String, defaultValue: String) = flow {
        getFlow(key).collect {
            emit(it ?: defaultValue)
        }
    }

}
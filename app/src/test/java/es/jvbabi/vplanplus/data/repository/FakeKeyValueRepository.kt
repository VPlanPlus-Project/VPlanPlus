package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class FakeKeyValueRepository : KeyValueRepository {
    private val data = mutableMapOf<String, String>()
    override suspend fun get(key: String): String? {
        return data[key]
    }

    override suspend fun set(key: String, value: String) {
        data[key] = value
    }

    override suspend fun getOrDefault(key: String, defaultValue: String): String {
        return data[key] ?: defaultValue
    }

    override suspend fun delete(key: String) {
        data.remove(key)
    }

    override fun getFlow(key: String): Flow<String?> = flow {
        while (true) {
            emit(data[key])
            delay(50)
        }
    }.distinctUntilChanged()

    override fun getFlowOrDefault(key: String, defaultValue: String): Flow<String> {
        return flow {
            while (true) {
                emit(data[key] ?: defaultValue)
                delay(50)
            }
        }.distinctUntilChanged()
    }

    override fun getOnMainThread(key: String): String? {
        return data[key]
    }
}
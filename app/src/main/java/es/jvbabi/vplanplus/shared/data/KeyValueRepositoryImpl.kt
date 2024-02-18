package es.jvbabi.vplanplus.shared.data

import android.util.Log
import es.jvbabi.vplanplus.data.source.database.dao.KeyValueDao
import es.jvbabi.vplanplus.domain.model.KeyValue
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class KeyValueRepositoryImpl(private val keyValueDao: KeyValueDao) : KeyValueRepository {
    override suspend fun get(key: String): String? {
        return keyValueDao.get(key = key)
    }

    override suspend fun set(key: String, value: String) {
        Log.d("KeyValueUseCases", "set: $key = $value")
        keyValueDao.set(KeyValue(id = key, value = value))
    }

    override fun getFlow(key: String): Flow<String?> {
        return keyValueDao.getFlow(key = key).distinctUntilChanged()
    }

    override suspend fun getOrDefault(key: String, defaultValue: String): String {
        return keyValueDao.get(key = key) ?: defaultValue
    }

    override suspend fun delete(key: String) {
        keyValueDao.delete(key = key)
    }
}
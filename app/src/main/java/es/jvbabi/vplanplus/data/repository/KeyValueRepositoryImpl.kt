package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.KeyValueDao
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class KeyValueRepositoryImpl(private val keyValueDao: KeyValueDao) : KeyValueRepository {
    override suspend fun get(key: String): String? {
        return keyValueDao.get(key = key)
    }

    override suspend fun set(key: String, value: String) {
        keyValueDao.set(key = key, value = value)
    }

    override fun getFlow(key: String) = keyValueDao.getFlow(key = key)

    override suspend fun getOrDefault(key: String, defaultValue: String): String {
        return keyValueDao.get(key = key) ?: defaultValue
    }
}
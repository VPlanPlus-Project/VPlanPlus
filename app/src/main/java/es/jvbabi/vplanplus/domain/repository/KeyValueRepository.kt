package es.jvbabi.vplanplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface KeyValueRepository {

    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)
    suspend fun getOrDefault(key: String, defaultValue: String): String
    suspend fun delete(key: String)
    fun getFlow(key: String): Flow<String?>
}
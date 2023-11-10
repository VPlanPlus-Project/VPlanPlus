package es.jvbabi.vplanplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface KeyValueRepository {

    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)
    fun getFlow(key: String): Flow<String?>
}
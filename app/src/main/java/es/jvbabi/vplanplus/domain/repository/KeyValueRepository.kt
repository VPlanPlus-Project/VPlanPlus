package es.jvbabi.vplanplus.domain.repository

interface KeyValueRepository {

    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)
}
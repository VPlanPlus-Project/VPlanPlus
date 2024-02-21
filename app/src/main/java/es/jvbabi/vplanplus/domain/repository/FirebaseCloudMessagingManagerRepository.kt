package es.jvbabi.vplanplus.domain.repository

interface FirebaseCloudMessagingManagerRepository {
    suspend fun updateToken(token: String): Boolean
}
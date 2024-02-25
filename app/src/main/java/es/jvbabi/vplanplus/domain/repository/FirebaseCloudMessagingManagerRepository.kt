package es.jvbabi.vplanplus.domain.repository

interface FirebaseCloudMessagingManagerRepository {
    suspend fun updateToken(t: String?): Boolean
}
package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessages(): Flow<List<Message>>
    fun getUnreadMessages(): Flow<List<Message>>
    suspend fun updateMessages(schoolId: Long?)
    suspend fun markMessageAsRead(messageId: String)
}
package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.VppId

interface FirebaseCloudMessagingManagerRepository {
    suspend fun addTokenUser(user: VppId.ActiveVppId, t: String): Boolean
    suspend fun addTokenGroup(group: Group, t: String): Boolean
    suspend fun resetToken(t: String): Boolean
}
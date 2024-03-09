package es.jvbabi.vplanplus.domain.usecase.home

import com.google.firebase.messaging.FirebaseMessaging
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.tasks.await

class SetUpUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
) {

    suspend operator fun invoke() {
        val lastUploadedToken = keyValueRepository.get(Keys.FCM_TOKEN) ?: ""
        val currentToken = FirebaseMessaging.getInstance().token.await() ?: ""
        if (lastUploadedToken != currentToken) {
            if (firebaseCloudMessagingManagerRepository.updateToken(currentToken)) keyValueRepository.set(Keys.FCM_TOKEN, currentToken)
        }

        keyValueRepository.set(Keys.IS_HOMEWORK_UPDATE_RUNNING, "false")
    }
}
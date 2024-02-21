package es.jvbabi.vplanplus.android.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository

    @Inject
    lateinit var keyValueRepository: KeyValueRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("PushNotificationService", "New token: $token")
            firebaseCloudMessagingManagerRepository.updateToken(token)
            keyValueRepository.set(Keys.FCM_TOKEN, token)
        }
    }
}
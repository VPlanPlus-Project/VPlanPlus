package es.jvbabi.vplanplus.android.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
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

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var schoolRepository: SchoolRepository

    @Inject
    lateinit var logRecordRepository: LogRecordRepository

    @Inject
    lateinit var homeworkRepository: HomeworkRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("PushNotificationService", "New token: $token")
            firebaseCloudMessagingManagerRepository.updateToken(token)
            keyValueRepository.set(Keys.FCM_TOKEN, token)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("PushNotificationService", "Message received: ${message.data["type"]}")

        val prefix = if (BuildConfig.DEBUG) "DEV_" else ""

        GlobalScope.launch {
            logRecordRepository.log("PushNotificationService", "Message received: ${message.data["type"]}\nDebug: ${BuildConfig.DEBUG}")
            when (message.data.getOrDefault("type", "")) {
                prefix + PushNotificationType.NEW_BOOKING -> {
                    schoolRepository.getSchools().forEach { school ->
                        roomRepository.fetchRoomBookings(school)
                    }
                }
                prefix + PushNotificationType.HOMEWORK_CHANGE -> {
                    homeworkRepository.fetchHomework()
                }
            }
        }
    }
}

data object PushNotificationType {
    const val NEW_BOOKING = "ROOM_BOOKED"
    const val HOMEWORK_CHANGE = "HOMEWORK_UPDATE"
}
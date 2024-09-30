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
import es.jvbabi.vplanplus.domain.usecase.sync.DoSyncUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.UpdateFirebaseTokenUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth.WebAuthTaskUseCases
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.UpdateHomeworkUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    @Inject
    lateinit var doSyncUseCase: DoSyncUseCase

    @Inject
    lateinit var updateHomeworkUseCase: UpdateHomeworkUseCase

    @Inject
    lateinit var updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase

    @Inject
    lateinit var webAuthTaskUseCases: WebAuthTaskUseCases

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("PushNotificationService", "New token: $token")
            updateFirebaseTokenUseCase.invoke(token)
            keyValueRepository.set(Keys.FCM_TOKEN, token)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("PushNotificationService", "Message received: ${message.data["type"]}")

        val isDebug = runBlocking { keyValueRepository.getOrDefault(Keys.FCM_DEBUG_MODE, BuildConfig.DEBUG.toString()).toBoolean() }

        val type = message.data["type"].let {
            if (isDebug) {
                if (it?.startsWith("DEV_") != true) return
                return@let it.removePrefix("DEV_")
            }
            return@let it
        }

        GlobalScope.launch {
            logRecordRepository.log("PushNotificationService", "Message received: ${message.data["type"]}\nDebug: $isDebug")
            when (type) {
                PushNotificationType.NEW_BOOKING -> {
                    schoolRepository.getSchools().forEach { school ->
                        roomRepository.fetchRoomBookings(school)
                    }
                }
                PushNotificationType.HOMEWORK_CHANGE -> updateHomeworkUseCase(true)
                PushNotificationType.UPDATE_PLAN -> doSyncUseCase()
                PushNotificationType.VPP_AUTH -> webAuthTaskUseCases.getWebAuthTaskUseCase()
            }
        }
    }
}

data object PushNotificationType {
    const val NEW_BOOKING = "ROOM_BOOKED"
    const val HOMEWORK_CHANGE = "HOMEWORK_UPDATE"
    const val UPDATE_PLAN = "UPDATE_PLAN"
    const val VPP_AUTH = "VPP_AUTH"
}
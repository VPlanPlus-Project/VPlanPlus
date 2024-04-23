package es.jvbabi.vplanplus.domain.usecase.home

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId

class SetUpUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val alarmManagerRepository: AlarmManagerRepository,
    private val homeworkRepository: HomeworkRepository,
    private val vppIdRepository: VppIdRepository,
    private val firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository,
    private val testForMissingVppIdToProfileConnectionsUseCase: TestForMissingVppIdToProfileConnectionsUseCase
) {

    suspend operator fun invoke() {
        try {
            testForInvalidSessions()
            keyValueRepository.set(Keys.MISSING_VPP_ID_TO_PROFILE_CONNECTION, testForMissingVppIdToProfileConnectionsUseCase().toString())
            updateFirebaseTokens()
            createHomeworkReminder()
        } catch (e: IOException) {
            Log.i("SetUpUseCase", "Error, Firebase services might not be available at the moment: ${e.message}")
        }
    }

    private suspend fun testForInvalidSessions() {
        val testMapping = vppIdRepository.getActiveVppIds().first().map {
            it to vppIdRepository.testVppIdSession(it)
        }

        testMapping.forEach { (vppId, isValid) ->
            if (isValid == false) vppIdRepository.unlinkVppId(vppId)
        }

        keyValueRepository.set(Keys.INVALID_VPP_SESSION, testMapping.filter { it.second != null }.any { it.second == false }.toString())
    }

    suspend fun createHomeworkReminder() {
        repeat(7) { day ->
            alarmManagerRepository.cancelAlarm("HOMEWORK_REMINDER_${day + 1}")
        }
        if (
            !keyValueRepository.getOrDefault(
                Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK,
                Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_DEFAULT
            ).toBoolean()
        ) return

        val defaultTime = keyValueRepository.getOrDefault(Keys.SETTINGS_PREFERRED_NOTIFICATION_TIME, Keys.SETTINGS_PREFERRED_NOTIFICATION_TIME_DEFAULT.toString()).toLong()

        val exceptions = homeworkRepository.getPreferredHomeworkNotificationTimes().first()
        val today = LocalDate.now()
        repeat(7) { day ->
            var dateTime = today.plusDays(day.toLong()).atTime(0, 0, 0, 0)

            val exception = exceptions.firstOrNull { it.dayOfWeek == dateTime.dayOfWeek }
            dateTime =
                if (exception != null) dateTime.plusSeconds(exception.secondsFromMidnight)
                else dateTime.plusSeconds(defaultTime)

            val epochSeconds = dateTime.atZone(ZoneId.systemDefault()).toInstant().epochSecond
            if (epochSeconds < System.currentTimeMillis() / 1000) return

            Log.i("SetUpUseCase", "Creating alarm for $day at $dateTime/$epochSeconds")

            alarmManagerRepository.setAlarm(
                epochSeconds,
                AlarmManagerRepository.TAG_HOMEWORK_NOTIFICATION,
                "HOMEWORK_REMINDER_${dateTime.dayOfWeek.value}",
            )
        }
    }

    private suspend fun updateFirebaseTokens() {
        val lastUploadedToken = keyValueRepository.get(Keys.FCM_TOKEN) ?: ""
        val currentToken = FirebaseMessaging.getInstance().token.await() ?: ""
        if (lastUploadedToken != currentToken) {
            if (firebaseCloudMessagingManagerRepository.updateToken(currentToken)) keyValueRepository.set(
                Keys.FCM_TOKEN,
                currentToken
            )
        }

        keyValueRepository.set(Keys.IS_HOMEWORK_UPDATE_RUNNING, "false")
    }
}
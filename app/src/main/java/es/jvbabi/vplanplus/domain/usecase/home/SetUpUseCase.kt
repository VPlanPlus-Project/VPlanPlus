package es.jvbabi.vplanplus.domain.usecase.home

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.daily.UpdateDailyNotificationAlarmsUseCase
import es.jvbabi.vplanplus.domain.usecase.general.CALENDAR_ASSESSMENT_FAB_BALLOON
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.UpdateFirebaseTokenUseCase
import es.jvbabi.vplanplus.domain.usecase.update.EnableAssessmentsOnlyForCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.update.EnableNewHomeDrawerUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.io.IOException

class SetUpUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val homeworkRepository: HomeworkRepository,
    private val vppIdRepository: VppIdRepository,
    private val testForMissingVppIdToProfileConnectionsUseCase: TestForMissingVppIdToProfileConnectionsUseCase,
    private val updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase,
    private val updateDailyNotificationAlarmsUseCase: UpdateDailyNotificationAlarmsUseCase,
    private val enableAssessmentsOnlyForCurrentProfileUseCase: EnableAssessmentsOnlyForCurrentProfileUseCase,
    private val setBalloonUseCase: SetBalloonUseCase,
    private val enableNewHomeDrawerUseCase: EnableNewHomeDrawerUseCase
) {
    suspend operator fun invoke() {
        try {
            postUpdateTasks()
            testForInvalidSessions()
            keyValueRepository.set(Keys.MISSING_VPP_ID_TO_PROFILE_CONNECTION, testForMissingVppIdToProfileConnectionsUseCase(true).toString())
            updateFirebaseTokens()
            createNextDayPreparationAlarms()
            homeworkRepository.updateHomeworkDocumentsFileState()
        } catch (e: IOException) {
            Log.i("SetUpUseCase", "Error, Firebase services might not be available at the moment: ${e.message}")
        }
    }

    private suspend fun postUpdateTasks() {
        val previousVersion = keyValueRepository.get(Keys.LAST_KNOWN_APP_VERSION)?.toIntOrNull() ?: 197 // 197 is the first version that supports this feature and therefore makes use of it
        val currentVersion = BuildConfig.VERSION_CODE

        if (previousVersion == currentVersion) return

        keyValueRepository.set(Keys.LAST_KNOWN_APP_VERSION, currentVersion.toString())

        if (previousVersion <= 328) enableAssessmentsOnlyForCurrentProfileUseCase()
        if (previousVersion <= 316) {
            setBalloonUseCase(CALENDAR_ASSESSMENT_FAB_BALLOON, true)
            enableNewHomeDrawerUseCase()
        }
    }

    private suspend fun testForInvalidSessions() {
        val testMapping = vppIdRepository.getActiveVppIds().first().map {
            it to vppIdRepository.testVppIdSession(it)
        }

        testMapping.forEach { (vppId, isValid) ->
            if (isValid == false) vppIdRepository.unlinkVppId(vppId)
        }

        if (testMapping.any { it.second == false }) {
            val fcmToken = keyValueRepository.get(Keys.FCM_TOKEN)
            if (fcmToken != null) updateFirebaseTokenUseCase(fcmToken)
        }

        keyValueRepository.set(Keys.INVALID_VPP_SESSION, testMapping.filter { it.second != null }.any { it.second == false }.toString())
    }

    private suspend fun createNextDayPreparationAlarms() {
        updateDailyNotificationAlarmsUseCase()
    }

    private suspend fun updateFirebaseTokens() {
        val lastUploadedToken = keyValueRepository.get(Keys.FCM_TOKEN) ?: ""
        val currentToken = FirebaseMessaging.getInstance().token.await() ?: ""
        if (lastUploadedToken != currentToken) {
            if (updateFirebaseTokenUseCase(currentToken)) keyValueRepository.set(
                Keys.FCM_TOKEN,
                currentToken
            )
        }

        keyValueRepository.set(Keys.IS_HOMEWORK_UPDATE_RUNNING, "false")
    }
}
package es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth

import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.repository.DoActionTask
import es.jvbabi.vplanplus.domain.repository.GetWebAuthResponse
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_DEFAULT_NOTIFICATION_ID_VPP_AUTH
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_SYSTEM
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.util.MathTools.cantor
import kotlinx.coroutines.flow.first

class GetWebAuthTaskUseCase(
    private val vppIdRepository: VppIdRepository,
    private val logRepository: LogRecordRepository,
    private val systemRepository: SystemRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository
) {
    suspend operator fun invoke() {
        val vppIds = vppIdRepository.getActiveVppIds().first()
        vppIds.forEach forEachVppId@{ vppId ->
            val task = vppIdRepository.getAuthTask(vppId)
            if (task.code == GetWebAuthResponse.ERROR) {
                logRepository.log("GetWebAuthTaskUseCase", "Error getting web auth task for VppId $vppId")
                return@forEachVppId
            }
            if (task.code == GetWebAuthResponse.NO_TASKS || task.value == null) return@forEachVppId

            if (!systemRepository.isAppInForeground()) {
                val payload = OpenTaskNotificationOnClickTaskPayload(
                    accountId = vppId.id,
                    taskId = task.value.taskId,
                    emojis = task.value.emojis,
                    validUntil = ZonedDateTimeConverter().zonedDateTimeToTimestamp(task.value.validUntil)
                )
                notificationRepository.sendNotification(
                    channelId = CHANNEL_ID_SYSTEM,
                    id = cantor(CHANNEL_DEFAULT_NOTIFICATION_ID_VPP_AUTH, vppId.id),
                    title = stringRepository.getString(R.string.notification_webAuthTitle),
                    message = stringRepository.getString(R.string.notification_webAuthMessage),
                    icon = R.drawable.vpp,
                    onClickTask = DoActionTask(tag = OPEN_TASK_NOTIFICATION_TAG, payload = Gson().toJson(payload)),
                    priority = NotificationCompat.PRIORITY_HIGH
                )
            }
        }
    }
}

const val OPEN_TASK_NOTIFICATION_TAG = "OPEN_TASK_NOTIFICATION"

data class OpenTaskNotificationOnClickTaskPayload(
    @SerializedName("account_id") val accountId: Int,
    @SerializedName("task_id") val taskId: Int,
    @SerializedName("emojis") val emojis: List<String>,
    @SerializedName("valid_until") val validUntil: Long
)
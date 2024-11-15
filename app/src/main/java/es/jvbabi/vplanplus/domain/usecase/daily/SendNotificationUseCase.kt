package es.jvbabi.vplanplus.domain.usecase.daily

import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.android.receiver.DailyRemindLaterReceiver
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.BroadcastIntentTask
import es.jvbabi.vplanplus.domain.repository.NotificationAction
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_DEFAULT_DAILY_ID
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_DAILY
import es.jvbabi.vplanplus.domain.repository.OpenScreenTask
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetNextDayUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsDeveloperModeEnabledUseCase
import es.jvbabi.vplanplus.ui.NotificationDestination
import es.jvbabi.vplanplus.util.MathTools.cantor
import es.jvbabi.vplanplus.util.maxLength
import es.jvbabi.vplanplus.util.removeAllSurrounding
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class SendNotificationUseCase(
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository,
    private val getNextDayUseCase: GetNextDayUseCase,
    private val isDeveloperModeEnabledUseCase: IsDeveloperModeEnabledUseCase
) {
    suspend operator fun invoke(profile: ClassProfile, dismissCounter: Int) {
        val day = getNextDayUseCase(profile = profile, fast = false).first()

        val homeworkForNextDay = day.homework
        val assessmentsForNextDay = day.actualExams()
        val lessons = day.actualLessons()

        val notificationText = buildString {
            if (isDeveloperModeEnabledUseCase().first()) {
                append("Dismiss:$dismissCounter; ")
            }
            if (day.lessons.isNotEmpty()) {
                append("\uD83D\uDCC5 " + day.date.format(DateTimeFormatter.ofPattern("EEEE, d. MMMM")))
                append("\n")
                append("\uD83D\uDD59 " + day.lessons.minOf { it.start }.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + " - " + day.lessons.maxOf { it.end }
                    .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                append(" ")
                val lessonCount = lessons.maxOf { it.lessonNumber } - lessons.minOf { it.lessonNumber } + 1
                append(" (")
                append(stringRepository.getPlural(R.plurals.dailyReminderNotification_lessons, lessonCount, lessonCount))
                append(")\n")
            }
            if (!day.info.isNullOrBlank()) {
                append("ℹ\uFE0F ")
                append(day.info.removeAllSurrounding('\n').maxLength(80))
                append("\n")
            }
            append("\n")
            if (homeworkForNextDay.isNotEmpty() && profile.isHomeworkEnabled) {
                append("\uD83D\uDCD3 ")
                append(stringRepository.getPlural(R.plurals.dailyReminderNotification_homework, homeworkForNextDay.size, "${homeworkForNextDay.count { it.allDone() }}/${homeworkForNextDay.size}"))
                append(" (")
                append(homeworkForNextDay.mapNotNull { it.homework.defaultLesson?.subject }.distinct().sorted().joinToString(", "))
                append(")")
                append("\n")
            }
            if (assessmentsForNextDay.isNotEmpty() && profile.isAssessmentsEnabled) {
                append("✍\uFE0F ")
                append(stringRepository.getPlural(R.plurals.dailyReminderNotification_assessments, assessmentsForNextDay.size, assessmentsForNextDay.size))
                append(" (")
                append(assessmentsForNextDay.mapNotNull { it.subject?.subject }.distinct().sorted().joinToString(", "))
                append(")")
                append("\n")
            }
        }.removeSuffix("\n")
        notificationRepository.sendNotification(
            channelId = CHANNEL_ID_DAILY,
            id = cantor(CHANNEL_DEFAULT_DAILY_ID, profile.id.toString().hashCode()),
            title = stringRepository.getString(R.string.dailyReminderNotification_title),
            subtitle = profile.displayName,
            message = notificationText,
            icon = R.drawable.vpp,
            onClickTask = OpenScreenTask(
                destination = Json.encodeToString(
                    NotificationDestination(
                        profileId = profile.id.toString(),
                        screen = "home",
                        payload = null
                    )
                )
            ),
            actions = listOfNotNull(
                NotificationAction(
                    title = stringRepository.getString(R.string.dailyReminderNotification_remind15Minutes),
                    task = BroadcastIntentTask(tag = DailyRemindLaterReceiver.TAG, payload = Json.encodeToString(DailyReminderNotificationData(profile.id.toString(), dismissCounter)))
                ),
                if (dismissCounter > 2) NotificationAction(
                    title = stringRepository.getString(R.string.dailyReminderNotification_updateTimes),
                    task = OpenScreenTask(destination = Json.encodeToString(
                        NotificationDestination(
                            profileId = profile.id.toString(),
                            screen = "settings/notification",
                            payload = null
                        )
                    ))
                ) else null
            ),
        )
    }
}

@Serializable
data class DailyReminderNotificationData(
    @SerialName("profile_id") val profileId: String,
    @SerialName("dismiss_counter") val dismissCounter: Int,
)
package es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase

import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.android.receiver.HomeworkRemindLaterReceiver
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.BroadcastIntentTask
import es.jvbabi.vplanplus.domain.repository.DoActionTask
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.NotificationAction
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_HOMEWORK_REMINDER_NOTIFICATION_ID
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_HOMEWORK
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime

class HomeworkReminderUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository,
) {
    suspend operator fun invoke() {
        if (!keyValueRepository.getOrDefault(
                Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK,
                Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_DEFAULT
            ).toBoolean()
        ) {
            return
        }

        val tomorrow = ZonedDateTime.now().plusDays(1)
        profileRepository
            .getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .filter { it.isHomeworkEnabled }
            .forEach { profile ->
                val homework = homeworkRepository
                    .getAllByProfile(profile).first()
                    .filter { profile.isDefaultLessonEnabled(it.homework.defaultLesson?.vpId) && (it as? PersonalizedHomework.CloudHomework)?.isHidden != true }
                val homeworkForTomorrow = homework.filter { it.homework.until.isBefore(tomorrow) && it.tasks.any { task -> !task.isDone } }
                if (homeworkForTomorrow.isEmpty()) return
                val homeworkForAfterTomorrow = homework.filter { it.homework.until.isAfter(tomorrow) && it.tasks.any { task -> !task.isDone } }
                val title = stringRepository.getString(R.string.notification_homeworkReminderTitle)
                val messageTomorrow = stringRepository.getPlural(
                    R.plurals.notification_homeworkReminderTomorrowMessage,
                    homeworkForTomorrow.size,
                    homeworkForTomorrow.size,
                    homeworkForTomorrow.map { it.homework.defaultLesson?.subject ?: stringRepository.getString(R.string.notification_homeworkReminderNoDefaultLesson) }.distinct().joinToString(", ")
                )
                val messageAfterTomorrow =
                    if (homeworkForTomorrow.isNotEmpty()) stringRepository.getPlural(
                        R.plurals.notification_homeworkReminderAfterTomorrowMessage,
                        homeworkForAfterTomorrow.size,
                        homeworkForAfterTomorrow.size
                    ) else ""
                val message = stringRepository.getString(
                    R.string.notification_homeworkReminderMessage,
                    messageTomorrow,
                    messageAfterTomorrow
                )
                val remindAgainAction = NotificationAction(
                    stringRepository.getString(
                        R.string.notification_homeworkReminderRemindAgain,
                        keyValueRepository.getOrDefault(
                            Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_LATER_SECONDS,
                            Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK_LATER_SECONDS_DEFAULT
                        ).toInt() / 60
                    ),
                    BroadcastIntentTask(HomeworkRemindLaterReceiver.TAG)
                )
                notificationRepository.sendNotification(
                    channelId = CHANNEL_ID_HOMEWORK,
                    id = CHANNEL_HOMEWORK_REMINDER_NOTIFICATION_ID,
                    title = title,
                    message = message,
                    icon = R.drawable.vpp,
                    onClickTask = DoActionTask(HomeworkRemindLaterReceiver.TAG),
                    actions = listOf(remindAgainAction)
                )
            }
    }
}
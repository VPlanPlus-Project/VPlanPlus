package es.jvbabi.vplanplus.feature.homework.shared.domain.usecase

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_HOMEWORK_REMINDER_NOTIFICATION_ID
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_HOMEWORK
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime

class HomeworkReminderUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository,
    private val context: Context
) {
    suspend operator fun invoke() {
        val homework = homeworkRepository.getAll().first()
        val tomorrow = ZonedDateTime.now().plusDays(1)
        val homeworkForTomorrow = homework.filter {
            it.until.isBefore(tomorrow) &&
            it.tasks.any { task -> !task.done }
        }

        if (homeworkForTomorrow.isEmpty()) return

        val homeworkForAfterTomorrow = homework.filter {
            it.until.isAfter(tomorrow) &&
            it.tasks.any { task -> !task.done }
        }

        val title = stringRepository.getString(R.string.notification_homeworkReminderTitle)
        val messageTomorrow = stringRepository.getPlural(
            R.plurals.notification_homeworkReminderTomorrowMessage,
            homeworkForTomorrow.size,
            homeworkForTomorrow.size,
            homeworkForTomorrow.joinToString(", ") { it.defaultLesson.subject }
        )

        val messageAfterTomorrow =
            if (homeworkForTomorrow.isNotEmpty()) " " + stringRepository.getPlural(
                R.plurals.notification_homeworkReminderAfterTomorrowMessage,
                homeworkForAfterTomorrow.size,
                homeworkForAfterTomorrow.size
            ) else ""

        val message = stringRepository.getString(
            R.string.notification_homeworkReminderMessage,
            messageTomorrow,
            messageAfterTomorrow
        )

        val intent = Intent(context, MainActivity::class.java)
            .putExtra("screen", Screen.HomeworkScreen.route)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        notificationRepository.sendNotification(
            CHANNEL_ID_HOMEWORK,
            CHANNEL_HOMEWORK_REMINDER_NOTIFICATION_ID,
            title,
            message,
            R.drawable.vpp,
            pendingIntent,
        )
    }
}
package es.jvbabi.vplanplus.feature.ndp.domain.usecase

import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_NDP
import es.jvbabi.vplanplus.domain.repository.OpenScreenTask
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetNextDayUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.DataType
import es.jvbabi.vplanplus.ui.NotificationDestination
import es.jvbabi.vplanplus.util.maxLength
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TriggerNdpReminderNotificationUseCase(
    private val profileRepository: ProfileRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository,
    private val examRepository: ExamRepository,
    private val getNextDayUseCase: GetNextDayUseCase
) {
    suspend operator fun invoke() {
        profileRepository
            .getProfiles()
            .first()
            .filterIsInstance<ClassProfile>()
            .forEach { profile ->
                val nextDay = getNextDayUseCase(profile, fast = false).first()

                val examsToGetNotifiedButNotTomorrow = examRepository
                    .getExams(profile = profile)
                    .first()
                    .filter { LocalDate.now().until(it.date).days in it.assessmentReminders.map { reminder -> reminder.daysBefore } }
                    .filter { it !in nextDay.exams }

                if (nextDay.homework.isEmpty() && nextDay.exams.isEmpty() && nextDay.lessons.isEmpty() && examsToGetNotifiedButNotTomorrow.isEmpty()) return@forEach

                notificationRepository.sendNotification(
                    channelId = CHANNEL_ID_NDP,
                    icon = R.drawable.vpp,
                    title = buildString {
                        if (nextDay.date.isEqual(LocalDate.now().plusDays(1))) append(stringRepository.getString(R.string.ndp_notificationTitleTomorrow))
                        else append(stringRepository.getString(
                                R.string.ndp_notificationTitle,
                                nextDay.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                            ))
                    },
                    subtitle = profile.displayName,
                    message = buildString {
                        if (nextDay.lessons.isNotEmpty() && nextDay.dataType == DataType.SUBSTITUTION_PLAN) {
                            append("\uD83D\uDD52 ")
                            append(nextDay.lessons.minOf { it.start }
                                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                            append(" - ")
                            append(nextDay.lessons.maxOf { it.end }
                                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                            append(" (")
                            val lessons = nextDay.lessons.map { it.lessonNumber }.toSet().size
                            append(
                                stringRepository.getPlural(
                                    R.plurals.ndp_notificationLessons,
                                    lessons,
                                    lessons
                                )
                            )
                            append(")")
                            append("\n")
                        }
                        if (!nextDay.info.isNullOrBlank()) {
                            append("ℹ\uFE0F ")
                            append(nextDay.info.maxLength(80))
                            append("\n")
                        }
                        if (nextDay.homework.isNotEmpty()) {
                            append("\uD83D\uDCDD ")
                            append(
                                stringRepository.getPlural(
                                    R.plurals.ndp_notificationHomework,
                                    nextDay.homework.size,
                                    nextDay.homework.size,
                                    nextDay.homework.mapNotNull { it.homework.defaultLesson?.subject }.joinToString(", ")
                                )
                            )
                            append("\n")
                        }
                        if (nextDay.exams.isNotEmpty()) {
                            append("✍\uFE0F ")
                            append(
                                stringRepository.getPlural(
                                    R.plurals.ndp_notificationAssessments,
                                    nextDay.exams.size,
                                    nextDay.exams.size,
                                    nextDay.exams.mapNotNull { it.subject?.subject }.joinToString(", ")
                                )
                            )
                            append("\n")
                        }
                        if (examsToGetNotifiedButNotTomorrow.isNotEmpty()) {
                            append("\uD83D\uDD14 ")
                            append(
                                stringRepository.getPlural(
                                    R.plurals.ndp_notificationExamsReminder,
                                    examsToGetNotifiedButNotTomorrow.size,
                                    examsToGetNotifiedButNotTomorrow.size
                                )
                            )
                            append("\n")
                        }

                        append("\n")
                        append("\t\uD83D\uDCF2 ")
                        append(stringRepository.getString(R.string.ndp_notificationMoreInfo))
                        removeSurrounding("\n")
                    },
                    id = 5,
                    onClickTask = OpenScreenTask(
                        destination = Json.encodeToString(
                            value = NotificationDestination(
                                screen = "ndp",
                                profileId = profile.id.toString(),
                                payload = null
                            )
                        )
                    )
                )
            }
    }
}
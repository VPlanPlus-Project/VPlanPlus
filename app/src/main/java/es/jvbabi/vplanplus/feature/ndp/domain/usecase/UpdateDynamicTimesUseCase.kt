package es.jvbabi.vplanplus.feature.ndp.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository.Companion.TAG_NDP_REMINDER
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import es.jvbabi.vplanplus.util.DateUtils.roundToMinutes
import es.jvbabi.vplanplus.util.median
import kotlinx.coroutines.flow.first
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

const val KEY_NDP_PREFIX = "NDP_USAGE_START."
fun DayOfWeek.toKey() = KEY_NDP_PREFIX + name

class UpdateDynamicTimesUseCase(
    private val lessonRepository: LessonRepository,
    private val timetableRepository: TimetableRepository,
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository,
    private val alarmManagerRepository: AlarmManagerRepository
) {
    suspend operator fun invoke() {
        val startOfWeek = LocalDate.now().atStartOfWeek()
        val version = keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
        profileRepository
            .getProfiles()
            .first()
            .filterIsInstance<ClassProfile>()
            .forEach { profile ->
                DayOfWeek
                    .entries
                    .filter { it != DayOfWeek.SATURDAY }
                    .forEach { dayOfWeek ->
                        val date = startOfWeek.with(dayOfWeek)
                        val lastLesson = lessonRepository.getLessonsForProfile(profile, date, version).first().let { substitutionPlanLessons ->
                            if (substitutionPlanLessons.isNullOrEmpty()) {
                                return@let timetableRepository.getTimetableForGroup(profile.group, date)
                            }
                            substitutionPlanLessons
                        }.maxByOrNull { it.lessonNumber }

                        val usageTimeOfPast = keyValueRepository
                            .get(dayOfWeek.toKey())
                            ?.let { it.split(",").map { time -> LocalTime.parse(time).toSecondOfDay() } }
                            ?.median()
                            ?.toLong()
                            ?.let { LocalTime.ofSecondOfDay(it) }

                        val reminderTime = (lastLesson?.end?.toLocalTime()?.plusMinutes((1.5*60).toLong()) ?: LocalTime.of(18, 0, 0, 0)).let { theoreticalReminderTime ->
                            if (usageTimeOfPast == null || usageTimeOfPast.isBefore(theoreticalReminderTime)) return@let theoreticalReminderTime

                            return@let ((theoreticalReminderTime.toSecondOfDay() + usageTimeOfPast.toSecondOfDay()) / 2).let {
                                LocalTime.ofSecondOfDay(it.toLong())
                            }
                        }.roundToMinutes(15, RoundingMode.CEILING)
                        Log.d("UpdateDynamicTimesUseCase", "Reminder time for ${profile.displayName} on $date (${dayOfWeek.name}) is $reminderTime")
                        val alarmTime = LocalDateTime.of(date, reminderTime).atZone(ZoneId.systemDefault())
                        alarmManagerRepository.setAlarm(alarmTime.toEpochSecond(), TAG_NDP_REMINDER, dayOfWeek.name)
                    }
            }
    }
}
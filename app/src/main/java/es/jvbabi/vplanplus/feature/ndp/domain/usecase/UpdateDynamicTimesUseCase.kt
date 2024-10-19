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
import es.jvbabi.vplanplus.feature.ndp.domain.repository.NdpUsageRepository
import es.jvbabi.vplanplus.util.DateUtils.roundToMinutes
import es.jvbabi.vplanplus.util.median
import kotlinx.coroutines.flow.first
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class UpdateDynamicTimesUseCase(
    private val lessonRepository: LessonRepository,
    private val timetableRepository: TimetableRepository,
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository,
    private val alarmManagerRepository: AlarmManagerRepository,
    private val ndpUsageRepository: NdpUsageRepository
) {
    suspend operator fun invoke() {
        alarmManagerRepository.deleteAlarmsByTag(TAG_NDP_REMINDER)
        val version = keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
        profileRepository
            .getProfiles()
            .first()
            .filterIsInstance<ClassProfile>()
            .forEach { profile ->
                repeat(7) { offset ->
                    val date = LocalDate.now().plusDays(offset.toLong())
                    val dayOfWeek = date.dayOfWeek
                    if (date.dayOfWeek == DayOfWeek.SATURDAY) return@repeat
                    val lastLesson = lessonRepository.getLessonsForProfile(profile, date, version).first().let { substitutionPlanLessons ->
                        if (substitutionPlanLessons.isNullOrEmpty()) {
                            return@let timetableRepository.getTimetableForGroup(profile.group, date)
                        }
                        substitutionPlanLessons
                    }.maxByOrNull { it.lessonNumber }

                    val usageTimeOfPast = ndpUsageRepository.getNdpStartsOfPast(profile, dayOfWeek)
                        .filter { it.toLocalDate().until(LocalDate.now(), ChronoUnit.DAYS) <= 32 }
                        .map { it.toLocalTime().toSecondOfDay() }
                        .median()
                        .let { if (it.isNaN()) null else LocalTime.ofSecondOfDay(it.toLong()) }

                    val endOfDay = lastLesson?.end?.toLocalTime()?.plusMinutes((1.5*60).toLong())
                    val reminderTime = (endOfDay ?: LocalTime.of(18, 0, 0, 0)).let { theoreticalReminderTime ->
                        if (usageTimeOfPast == null || (endOfDay != null && usageTimeOfPast.isBefore(theoreticalReminderTime))) return@let theoreticalReminderTime

                        return@let ((theoreticalReminderTime.toSecondOfDay() + usageTimeOfPast.toSecondOfDay()) / 2).let {
                            LocalTime.ofSecondOfDay(it.toLong())
                        }
                    }.roundToMinutes(15, RoundingMode.CEILING).withSecond(0)
                    val alarmTime = LocalDateTime.of(date, reminderTime).atZone(ZoneId.systemDefault())
                    if (alarmTime.isBefore(ZonedDateTime.now())) return
                    Log.d("UpdateDynamicTimesUseCase", "Reminder time for ${profile.displayName} on $date (${dayOfWeek.name}) is $reminderTime")
                    alarmManagerRepository.addAlarm(
                        time = alarmTime,
                        tags = listOf(TAG_NDP_REMINDER, dayOfWeek.name),
                        data = profile.id.toString()
                    )
                }
        }
    }
}
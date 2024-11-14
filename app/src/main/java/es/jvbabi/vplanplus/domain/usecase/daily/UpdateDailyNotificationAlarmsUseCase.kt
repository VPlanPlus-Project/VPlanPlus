package es.jvbabi.vplanplus.domain.usecase.daily

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class UpdateDailyNotificationAlarmsUseCase(
    private val alarmManagerRepository: AlarmManagerRepository,
    private val profileRepository: ProfileRepository,
    private val dailyReminderRepository: DailyReminderRepository
) {
    suspend operator fun invoke() {
        alarmManagerRepository.deleteIf { AlarmManagerRepository.TAG_DAILY_REMINDER in it.tags && AlarmManagerRepository.TAG_DAILY_REMINDER_DELAYED !in it.tags }
        repeat(7) { dayOffset ->
            val date = LocalDate.now().plusDays(dayOffset.toLong())
            if (date.dayOfWeek == DayOfWeek.SATURDAY) return@repeat
            profileRepository
                .getProfiles()
                .first()
                .filterIsInstance<ClassProfile>()
                .filter { it.isDailyNotificationEnabled }
                .forEach { profile ->
                    val time = dailyReminderRepository.getDailyReminderTime(profile, date.dayOfWeek).first()
                    val datetime = LocalDateTime.of(date, time)
                    if (datetime.isBefore(LocalDateTime.now())) return@forEach
                    alarmManagerRepository.addAlarm(
                        time = datetime.atZone(ZoneId.systemDefault()),
                        tags = listOf(AlarmManagerRepository.TAG_DAILY_REMINDER),
                        data = Json.encodeToString(DailyReminderNotificationData(profile.id.toString(), 0)),
                    )
                }
        }
    }
}
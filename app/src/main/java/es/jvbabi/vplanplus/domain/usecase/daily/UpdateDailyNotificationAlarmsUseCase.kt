package es.jvbabi.vplanplus.domain.usecase.daily

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class UpdateDailyNotificationAlarmsUseCase(
    private val alarmManagerRepository: AlarmManagerRepository,
    private val profileRepository: ProfileRepository,
    private val dailyReminderRepository: DailyReminderRepository
) {
    suspend operator fun invoke() {
        alarmManagerRepository.deleteAlarmsByTag(AlarmManagerRepository.TAG_DAILY_REMINDER)
        repeat(7) { dayOffset ->
            val date = LocalDate.now().plusDays(dayOffset.toLong())
            profileRepository
                .getProfiles()
                .first()
                .filterIsInstance<ClassProfile>()
                .filter { it.isDailyNotificationEnabled }
                .forEach { profile ->
                    val time = dailyReminderRepository.getDailyReminderTime(profile, date.dayOfWeek).first()
                    val datetime = LocalDateTime.of(date, time)
                    alarmManagerRepository.addAlarm(
                        time = datetime.atZone(ZoneId.systemDefault()),
                        tags = listOf(AlarmManagerRepository.TAG_DAILY_REMINDER),
                        data = profile.id.toString()
                    )
                }
        }
    }
}
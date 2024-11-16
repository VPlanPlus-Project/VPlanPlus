package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalTime

class DailyReminderRepositoryImpl(
    private val keyValueRepository: KeyValueRepository
) : DailyReminderRepository {
    override fun getDailyReminderTime(profile: ClassProfile, dayOfWeek: DayOfWeek): Flow<LocalTime> {
        return keyValueRepository.getFlow(Keys.dailyReminderTime(profile, dayOfWeek)).map {
            if (it == null) return@map LocalTime.of(15, 30)
            LocalTime.ofSecondOfDay(it.toLong())
        }
    }

    override suspend fun setDailyReminderTime(profile: ClassProfile, dayOfWeek: DayOfWeek, time: LocalTime) {
        keyValueRepository.set(Keys.dailyReminderTime(profile, dayOfWeek), time.toSecondOfDay().toString())
    }
}
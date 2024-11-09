package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.ClassProfile
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalTime

interface DailyReminderRepository {
    fun getDailyReminderTime(profile: ClassProfile, dayOfWeek: DayOfWeek): Flow<LocalTime>
    suspend fun setDailyReminderTime(profile: ClassProfile, dayOfWeek: DayOfWeek, time: LocalTime)
    fun isDailyReminderEnabled(profile: ClassProfile): Flow<Boolean>
    suspend fun setDailyReminderEnabled(profile: ClassProfile, enabled: Boolean)
}
package es.jvbabi.vplanplus.feature.ndp.domain.repository

import es.jvbabi.vplanplus.domain.model.ClassProfile
import java.time.DayOfWeek
import java.time.LocalDateTime

interface NdpUsageRepository {
    suspend fun startNdp(profile: ClassProfile)
    suspend fun finishNdp(profile: ClassProfile)
    suspend fun getNdpStartsOfPast(profile: ClassProfile, dayOfWeek: DayOfWeek): List<LocalDateTime>
}
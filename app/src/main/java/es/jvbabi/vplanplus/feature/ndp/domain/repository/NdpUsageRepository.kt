package es.jvbabi.vplanplus.feature.ndp.domain.repository

import es.jvbabi.vplanplus.domain.model.ClassProfile

interface NdpUsageRepository {
    suspend fun startNdp(profile: ClassProfile)
    suspend fun finishNdp(profile: ClassProfile)
}
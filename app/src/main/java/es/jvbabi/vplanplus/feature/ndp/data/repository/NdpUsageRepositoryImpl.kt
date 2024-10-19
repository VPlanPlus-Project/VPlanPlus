package es.jvbabi.vplanplus.feature.ndp.data.repository

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.ndp.data.dao.NdpUsageDao
import es.jvbabi.vplanplus.feature.ndp.domain.repository.NdpUsageRepository
import java.time.LocalDate
import java.time.LocalTime

class NdpUsageRepositoryImpl(
    private val ndpUsageDao: NdpUsageDao
) : NdpUsageRepository {
    override suspend fun startNdp(profile: ClassProfile) {
        if (ndpUsageDao.isNdpFinished(profile.id, LocalDate.now()) == true) return
        ndpUsageDao.insert(
            profileId = profile.id,
            date = LocalDate.now(),
            time = LocalTime.now(),
            hasCompleted = false
        )
    }

    override suspend fun finishNdp(profile: ClassProfile) {
        ndpUsageDao.finishNdp(profile.id, LocalDate.now())
    }
}
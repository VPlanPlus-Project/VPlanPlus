package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.ProfileDao
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao
): ProfileRepository {
    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfiles()
    }
}
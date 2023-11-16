package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao
): ProfileRepository {
    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfiles()
    }

    override suspend fun createProfile(referenceId: Long, type: ProfileType, name: String, customName: String) {
        profileDao.insert(Profile(referenceId = referenceId, type = type, name = name, customName = customName, calendarMode = ProfileCalendarType.NONE, calendarId = null))
    }

    override suspend fun getProfileByReferenceId(referenceId: Long, type: ProfileType): Profile {
        return profileDao.getProfileByReferenceId(referenceId = referenceId, type = type)
    }

    override fun getProfileById(id: Long): Flow<Profile> {
        return profileDao.getProfileById(id = id)
    }

    override suspend fun deleteProfile(profileId: Long) {
        profileDao.deleteProfile(profileId = profileId)
    }

    override suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile> {
        return profileDao.getProfilesBySchoolId(schoolId = schoolId)
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.insert(profile = profile)
    }
}
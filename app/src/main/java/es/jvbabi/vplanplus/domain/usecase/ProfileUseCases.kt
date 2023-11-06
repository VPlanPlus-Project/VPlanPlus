package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class ProfileUseCases(
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository
) {

    suspend fun createStudentProfile(classId: Long, name: String) {
        profileRepository.createProfile(referenceId = classId, type = 0, name = name)
    }

    suspend fun getProfileByClassId(classId: Long): Profile {
        return profileRepository.getProfileByReferenceId(referenceId = classId, type = 0)
    }

    suspend fun getActiveProfile(): Profile? {
        val activeProfileId = keyValueRepository.get(key = Keys.ACTIVE_PROFILE.name) ?: return null
        return profileRepository.getProfileById(id = activeProfileId.toLong())
    }

    suspend fun getProfiles(): List<Profile> {
        return profileRepository.getProfiles()
    }

    suspend fun setActiveProfile(profileId: Long) {
        keyValueRepository.set(key = Keys.ACTIVE_PROFILE.name, value = profileId.toString())
    }

    suspend fun deleteProfile(profileId: Long) {
        profileRepository.deleteProfile(profileId = profileId)
    }
}
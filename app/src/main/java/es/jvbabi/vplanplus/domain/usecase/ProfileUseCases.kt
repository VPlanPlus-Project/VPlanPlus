package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository

class ProfileUseCases(
    private val profileRepository: ProfileRepository,
    private val schoolRepository: SchoolRepository,
    private val classRepository: ClassRepository,
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

    suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile> {
        return profileRepository.getProfilesBySchoolId(schoolId = schoolId)
    }

    suspend fun getSchoolFromProfileId(profileId: Long): School {
        val profile = profileRepository.getProfileById(id = profileId)
        return when (profile.type) {
            0 -> {
                val `class` = classRepository.getClassById(id = profile.referenceId)
                schoolRepository.getSchoolFromId(schoolId = `class`.schoolId)
            }
            else -> {
                TODO("This should never happen")
            }
        }
    }
}
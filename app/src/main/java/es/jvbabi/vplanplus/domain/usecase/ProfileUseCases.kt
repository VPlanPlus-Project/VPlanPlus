package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository

class ProfileUseCases(
    private val profileRepository: ProfileRepository,
    private val schoolRepository: SchoolRepository,
    private val classRepository: ClassRepository,
    private val keyValueRepository: KeyValueRepository,
    private val teacherRepository: TeacherRepository
) {

    suspend fun createStudentProfile(classId: Long, name: String) {
        profileRepository.createProfile(referenceId = classId, type = ProfileType.STUDENT, name = name)
    }

    suspend fun createTeacherProfile(teacherId: Long, name: String) {
        profileRepository.createProfile(referenceId = teacherId, type = ProfileType.TEACHER, name = name)
    }

    suspend fun getProfileByClassId(classId: Long): Profile {
        return profileRepository.getProfileByReferenceId(referenceId = classId, type = ProfileType.STUDENT)
    }

    suspend fun getProfileByTeacherId(teacherId: Long): Profile {
        return profileRepository.getProfileByReferenceId(referenceId = teacherId, type = ProfileType.TEACHER)
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
            ProfileType.STUDENT -> {
                val `class` = classRepository.getClassById(id = profile.referenceId)
                schoolRepository.getSchoolFromId(schoolId = `class`.schoolId)
            }
            ProfileType.TEACHER -> {
                val teacher = teacherRepository.getTeacherById(id = profile.referenceId)
                schoolRepository.getSchoolFromId(schoolId = teacher!!.schoolId)
            }
            ProfileType.ROOM -> {
                TODO()
            }
        }
    }
}
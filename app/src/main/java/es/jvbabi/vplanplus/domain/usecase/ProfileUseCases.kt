package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class ProfileUseCases(
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository,
    private val keyValueRepository: KeyValueRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val calendarRepository: CalendarRepository,
) {

    suspend fun deleteDefaultLessonsFromProfile(profileId: UUID) {
        profileRepository.deleteDefaultLessonsFromProfile(profileId = profileId)
    }

    suspend fun createStudentProfile(classId: UUID, name: String): UUID {
        return profileRepository.createProfile(
            referenceId = classId,
            type = ProfileType.STUDENT,
            name = name,
            customName = name
        )
    }

    suspend fun createTeacherProfile(teacherId: UUID, name: String) {
        profileRepository.createProfile(
            referenceId = teacherId,
            type = ProfileType.TEACHER,
            name = name,
            customName = name
        )
    }

    suspend fun setCalendarType(profileId: UUID, calendarType: ProfileCalendarType) {
        profileRepository.updateProfile(
            profileRepository.getDbProfileById(profileId = profileId)!!
                .copy(calendarMode = calendarType)
        )
    }

    suspend fun setCalendarId(profileId: UUID, calendarId: Long) {
        profileRepository.updateProfile(
            profileRepository.getDbProfileById(profileId = profileId)!!.copy(calendarId = calendarId)
        )
    }

    suspend fun setDisplayName(profileId: UUID, displayName: String) {
        profileRepository.updateProfile(
            profileRepository.getDbProfileById(profileId = profileId)!!.copy(customName = displayName)
        )
    }

    suspend fun enableDefaultLesson(profileId: UUID, vpId: Long) {
        profileRepository.enableDefaultLesson(vpId = vpId, profileId = profileId)
    }

    suspend fun disableDefaultLesson(profileId: UUID, vpId: Long) {
        profileRepository.disableDefaultLesson(profileId = profileId, vpId = vpId)
    }

    suspend fun createRoomProfile(roomId: UUID, name: String) {
        profileRepository.createProfile(
            referenceId = roomId,
            type = ProfileType.ROOM,
            name = name,
            customName = name
        )
    }

    suspend fun getProfileByTeacherId(teacherId: UUID): Profile {
        return profileRepository.getProfileByReferenceId(
            referenceId = teacherId,
            type = ProfileType.TEACHER
        )
    }

    suspend fun getProfileByRoomId(roomId: UUID): Profile {
        return profileRepository.getProfileByReferenceId(
            referenceId = roomId,
            type = ProfileType.ROOM
        )
    }

    suspend fun getActiveProfile(): Profile? {
        val id = keyValueRepository.get(key = Keys.ACTIVE_PROFILE) ?: return null
        val activeProfileId = UUID.fromString(id)
        return profileRepository.getProfileById(id = activeProfileId).first()
    }

    fun getProfiles(): Flow<List<Profile>> {
        return profileRepository.getProfiles()
    }

    suspend fun deleteProfile(profileId: UUID) {
        profileRepository.deleteProfile(profileId = profileId)
    }

    suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile> {
        return profileRepository.getProfilesBySchoolId(schoolId = schoolId)
    }

    suspend fun getSchoolFromProfileId(profileId: UUID): School {
        val profile = profileRepository.getProfileById(id = profileId).first()
        return when (profile!!.type) {
            ProfileType.STUDENT -> classRepository.getClassById(id = profile.referenceId)!!.school
            ProfileType.TEACHER -> teacherRepository.getTeacherById(id = profile.referenceId)!!.school
            ProfileType.ROOM -> roomRepository.getRoomById(profile.referenceId)!!.school
        }
    }

    fun getProfileById(profileId: UUID): Flow<Profile?> {
        return profileRepository.getProfileById(id = profileId)
    }

    suspend fun getCalendarFromProfile(profile: Profile): Calendar? {
        if (profile.calendarId == null) return null
        return calendarRepository.getCalendarById(id = profile.calendarId)
    }
}
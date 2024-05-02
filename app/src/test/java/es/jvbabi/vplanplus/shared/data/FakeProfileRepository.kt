package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class FakeProfileRepository : ProfileRepository {
    private val profiles = mutableMapOf<Profile, List<Pair<DefaultLesson, Boolean>>>()

    override fun getProfiles(): Flow<List<Profile>> {
        return flowOf(profiles.keys.toList())
    }

    override suspend fun createProfile(
        referenceId: UUID,
        type: ProfileType,
        name: String,
        customName: String
    ): UUID {
        val id = UUID.randomUUID()
        profiles[Profile(
            id = id,
            referenceId = referenceId,
            type = type,
            originalName = name,
            displayName = customName,
            defaultLessons = emptyMap(),
            calendarId = null,
            calendarType = ProfileCalendarType.NONE,
            vppId = null
        )] = emptyList()
        return id
    }

    override suspend fun getProfileByReferenceId(referenceId: UUID, type: ProfileType): Profile {
        return profiles.keys.first { it.referenceId == referenceId && it.type == type }
    }

    override fun getProfileById(id: UUID): Flow<Profile?> {
        return flowOf(profiles.keys.firstOrNull { it.id == id })
    }

    override suspend fun deleteProfile(profileId: UUID) {
        profiles.remove(getProfileById(profileId).first())
    }

    override suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile> {
        return profiles.keys.filter { it.referenceId == UUID.fromString(schoolId.toString()) }
    }

    override suspend fun updateProfile(profile: DbProfile) {
        TODO()
    }

    override suspend fun getDbProfileById(profileId: UUID): DbProfile? {
        TODO()
    }

    override suspend fun enableDefaultLesson(profileId: UUID, vpId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun disableDefaultLesson(profileId: UUID, vpId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDefaultLessonsFromProfile(profileId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDefaultLessonFromProfile(vpId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getSchoolFromProfile(profile: Profile): School {
        TODO("Not yet implemented")
    }

    override suspend fun getActiveProfile(): Flow<Profile?> {
        TODO("Not yet implemented")
    }

    override suspend fun setProfileVppId(profile: Profile, vppId: VppId?) {
        TODO("Not yet implemented")
    }
}
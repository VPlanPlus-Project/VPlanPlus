package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FakeProfileRepository : ProfileRepository {
    override fun getProfilesBySchool(schoolId: Int): Flow<List<Profile>> {
        TODO("Not yet implemented")
    }

    override fun getProfiles(): Flow<List<Profile>> {
        TODO("Not yet implemented")
    }

    override fun getProfileById(profileId: UUID): Flow<Profile?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProfile(profile: Profile) {
        TODO("Not yet implemented")
    }

    override suspend fun createClassProfile(
        profileId: UUID,
        group: Group,
        name: String,
        customName: String,
        calendar: Calendar?,
        calendarType: ProfileCalendarType,
        isHomeworkEnabled: Boolean,
        vppId: VppId?
    ): UUID {
        TODO("Not yet implemented")
    }

    override suspend fun createTeacherProfile(
        profileId: UUID,
        teacher: Teacher,
        name: String,
        customName: String,
        calendar: Calendar?,
        calendarType: ProfileCalendarType
    ): UUID {
        TODO("Not yet implemented")
    }

    override suspend fun createRoomProfile(
        profileId: UUID,
        room: Room,
        name: String,
        customName: String,
        calendar: Calendar?,
        calendarType: ProfileCalendarType
    ): UUID {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDefaultLessons(profile: ClassProfile) {
        TODO("Not yet implemented")
    }

    override suspend fun setDefaultLessonActivationState(
        classProfileId: UUID,
        defaultLessonVpId: Int,
        activate: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDefaultLessonStatesFromProfile(classProfile: ClassProfile) {
        TODO("Not yet implemented")
    }

    override suspend fun setHomeworkEnabled(profile: ClassProfile, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setVppIdForProfile(classProfile: ClassProfile, vppId: VppId.ActiveVppId?) {
        TODO("Not yet implemented")
    }

    override suspend fun setCalendarIdForProfile(profile: Profile, calendarId: Long?) {
        TODO("Not yet implemented")
    }

    override suspend fun setCalendarModeForProfile(
        profile: Profile,
        calendarMode: ProfileCalendarType
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun setProfileDisplayName(profile: Profile, displayName: String?) {
        TODO("Not yet implemented")
    }

}
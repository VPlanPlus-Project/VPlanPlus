package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.VppId
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProfileRepository {

    fun getProfilesBySchool(schoolId: Int): Flow<List<Profile>>
    fun getProfiles(): Flow<List<Profile>>
    fun getProfileById(profileId: UUID): Flow<Profile?>

    suspend fun deleteProfile(profile: Profile)

    suspend fun createClassProfile(
        profileId: UUID = UUID.randomUUID(),
        group: Group,
        name: String = group.name,
        customName: String = group.name,
        calendar: Calendar? = null,
        calendarType: ProfileCalendarType = ProfileCalendarType.NONE,
        isHomeworkEnabled: Boolean,
        vppId: VppId? = null
    ): UUID

    suspend fun createTeacherProfile(
        profileId: UUID = UUID.randomUUID(),
        teacher: Teacher,
        name: String = teacher.acronym,
        customName: String = teacher.acronym,
        calendar: Calendar? = null,
        calendarType: ProfileCalendarType = ProfileCalendarType.NONE,
    ): UUID

    suspend fun createRoomProfile(
        profileId: UUID = UUID.randomUUID(),
        room: Room,
        name: String = room.name,
        customName: String = room.name,
        calendar: Calendar? = null,
        calendarType: ProfileCalendarType = ProfileCalendarType.NONE,
    ): UUID

    suspend fun deleteDefaultLessons(profile: ClassProfile)
    suspend fun setDefaultLessonActivationState(classProfileId: UUID, defaultLessonVpId: Int, activate: Boolean)
    suspend fun deleteDefaultLessonStatesFromProfile(classProfile: ClassProfile)

    suspend fun setHomeworkEnabled(profile: ClassProfile, enabled: Boolean)
    suspend fun setVppIdForProfile(classProfile: ClassProfile, vppId: VppId?)
    suspend fun setCalendarIdForProfile(profile: Profile, calendarId: Long?)
    suspend fun setCalendarModeForProfile(profile: Profile, calendarMode: ProfileCalendarType)
    suspend fun setProfileDisplayName(profile: Profile, displayName: String?)
}
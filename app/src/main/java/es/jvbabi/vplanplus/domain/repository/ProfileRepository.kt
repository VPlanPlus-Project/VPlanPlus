package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.ClassProfileNotificationSetting
import es.jvbabi.vplanplus.domain.model.CommonProfileNotificationSetting
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomProfile
import es.jvbabi.vplanplus.domain.model.RoomProfileNotificationSetting
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.TeacherProfile
import es.jvbabi.vplanplus.domain.model.TeacherProfileNotificationSetting
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
        isAssessmentsEnabled: Boolean,
        isNotificationsEnabled: Boolean,
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
    suspend fun setAssessmentEnabled(profile: ClassProfile, enabled: Boolean)
    suspend fun setVppIdForProfile(classProfile: ClassProfile, vppId: VppId.ActiveVppId?)
    suspend fun setCalendarIdForProfile(profile: Profile, calendarId: Long?)
    suspend fun setCalendarModeForProfile(profile: Profile, calendarMode: ProfileCalendarType)
    suspend fun setProfileDisplayName(profile: Profile, displayName: String?)

    suspend fun updateProfile(
        profile: Profile,
        displayName: String = profile.displayName,
        calendarMode: ProfileCalendarType = profile.calendarType,
        calendarId: Long? = profile.calendarId,
        notificationSettings: CommonProfileNotificationSetting = profile.notificationSettings,
        isNotificationEnabled: Boolean = profile.notificationsEnabled
    ) {
        when (profile) {
            is ClassProfile -> {
                assert(notificationSettings is ClassProfileNotificationSetting)
                updateClassProfile(
                    profile = profile,
                    displayName = displayName,
                    calendarMode = calendarMode,
                    calendarId = calendarId,
                    notificationSettings = notificationSettings as ClassProfileNotificationSetting,
                    isNotificationEnabled = isNotificationEnabled
                )
            }
            is TeacherProfile -> {
                assert(notificationSettings is TeacherProfileNotificationSetting)
                updateTeacherProfile(
                    profile = profile,
                    displayName = displayName,
                    calendarMode = calendarMode,
                    calendarId = calendarId,
                    notificationSettings = notificationSettings as TeacherProfileNotificationSetting,
                    isNotificationEnabled = isNotificationEnabled
                )
            }
            is RoomProfile -> {
                assert(notificationSettings is RoomProfileNotificationSetting)
                updateRoomProfile(
                    profile = profile,
                    displayName = displayName,
                    calendarMode = calendarMode,
                    calendarId = calendarId,
                    notificationSettings = notificationSettings as RoomProfileNotificationSetting,
                    isNotificationEnabled = isNotificationEnabled
                )
            }
        }
    }

    suspend fun updateClassProfile(
        profile: ClassProfile,
        displayName: String = profile.displayName,
        calendarMode: ProfileCalendarType = profile.calendarType,
        calendarId: Long? = profile.calendarId,
        isHomeworkEnabled: Boolean = profile.isHomeworkEnabled,
        isAssessmentsEnabled: Boolean = profile.isAssessmentsEnabled,
        vppId: VppId.ActiveVppId? = profile.vppId,
        isNotificationEnabled: Boolean = profile.notificationsEnabled,
        notificationSettings: ClassProfileNotificationSetting = profile.notificationSettings
    )

    suspend fun updateTeacherProfile(
        profile: TeacherProfile,
        displayName: String = profile.displayName,
        calendarMode: ProfileCalendarType = profile.calendarType,
        calendarId: Long? = profile.calendarId,
        isNotificationEnabled: Boolean = profile.notificationsEnabled,
        notificationSettings: TeacherProfileNotificationSetting = profile.notificationSettings
    )

    suspend fun updateRoomProfile(
        profile: RoomProfile,
        displayName: String = profile.displayName,
        calendarMode: ProfileCalendarType = profile.calendarType,
        calendarId: Long? = profile.calendarId,
        isNotificationEnabled: Boolean = profile.notificationsEnabled,
        notificationSettings: RoomProfileNotificationSetting = profile.notificationSettings
    )
}
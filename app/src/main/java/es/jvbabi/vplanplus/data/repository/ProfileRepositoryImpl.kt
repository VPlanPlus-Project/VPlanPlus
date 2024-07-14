package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDefaultLessonsCrossoverDao
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomProfile
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.TeacherProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.util.UUID

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val profileDefaultLessonsCrossoverDao: ProfileDefaultLessonsCrossoverDao,
) : ProfileRepository {

    override fun getProfilesBySchool(schoolId: Int): Flow<List<Profile>> = flow {
        combine(
            profileDao.getClassProfiles(),
            profileDao.getTeacherProfiles(),
            profileDao.getRoomProfiles()
        ) { classProfiles, teacherProfiles, roomProfiles ->
            val profiles = mutableListOf<Profile>()
            classProfiles.filter { it.group.school.id == schoolId }.forEach { profiles.add(it.toModel()) }
            teacherProfiles.filter { it.schoolEntity.school.id == schoolId }.forEach { profiles.add(it.toModel()) }
            roomProfiles.filter { it.room.school.id == schoolId }.forEach { profiles.add(it.toModel()) }
            profiles
        }.collect {
            emit(it)
        }
    }

    override fun getProfiles() = flow {
        combine(
            profileDao.getClassProfiles(),
            profileDao.getTeacherProfiles(),
            profileDao.getRoomProfiles()
        ) { classProfiles, teacherProfiles, roomProfiles ->
            val profiles = mutableListOf<Profile>()
            classProfiles.forEach { profiles.add(it.toModel()) }
            teacherProfiles.forEach { profiles.add(it.toModel()) }
            roomProfiles.forEach { profiles.add(it.toModel()) }
            profiles
        }.collect {
            emit(it)
        }
    }

    override suspend fun deleteProfile(profile: Profile) {
        when (profile) {
            is ClassProfile -> profileDao.deleteClassProfile(profile.id)
            is TeacherProfile -> profileDao.deleteTeacherProfile(profile.id)
            is RoomProfile -> profileDao.deleteRoomProfile(profile.id)
        }
    }

    override fun getProfileById(profileId: UUID) = flow {
        combine(
            profileDao.getClassProfiles(),
            profileDao.getTeacherProfiles(),
            profileDao.getRoomProfiles()
        ) { classProfiles, teacherProfiles, roomProfiles ->
            val profile = classProfiles.firstOrNull { it.classProfile.id == profileId }?.toModel()
                ?: teacherProfiles.firstOrNull { it.teacherProfile.id == profileId }?.toModel()
                ?: roomProfiles.firstOrNull { it.roomProfile.id == profileId }?.toModel()
            profile
        }.collect { profile ->
            emit(profile)
        }
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
        val calendarId = calendar?.id
        val classId = group.groupId
        val vppIdInt = vppId?.id
        profileDao.createClassProfile(
            id = profileId,
            name = name,
            customName = customName,
            calendarMode = calendarType,
            calendarId = calendarId,
            classId = classId,
            isHomeworkEnabled = isHomeworkEnabled,
            vppId = vppIdInt
        )
        return profileId
    }

    override suspend fun createTeacherProfile(
        profileId: UUID,
        teacher: Teacher,
        name: String,
        customName: String,
        calendar: Calendar?,
        calendarType: ProfileCalendarType
    ): UUID {
        val calendarId = calendar?.id
        val teacherId = teacher.teacherId
        profileDao.createTeacherProfile(
            id = profileId,
            name = name,
            customName = customName,
            calendarMode = calendarType,
            calendarId = calendarId,
            teacherId = teacherId
        )
        return profileId
    }

    override suspend fun createRoomProfile(
        profileId: UUID,
        room: Room,
        name: String,
        customName: String,
        calendar: Calendar?,
        calendarType: ProfileCalendarType
    ): UUID {
        val calendarId = calendar?.id
        val roomId = room.roomId
        profileDao.createRoomProfile(
            id = profileId,
            name = name,
            customName = customName,
            calendarMode = calendarType,
            calendarId = calendarId,
            roomId = roomId
        )
        return profileId
    }

    override suspend fun deleteDefaultLessons(profile: ClassProfile) {
        profileDefaultLessonsCrossoverDao.deleteCrossoversByProfileId(profile.id)
    }

    override suspend fun setDefaultLessonActivationState(classProfileId: UUID, defaultLessonVpId: Int, activate: Boolean) {
        profileDefaultLessonsCrossoverDao.insertCrossover(DbProfileDefaultLesson(classProfileId, defaultLessonVpId.toLong(), activate))
    }

    override suspend fun deleteDefaultLessonStatesFromProfile(classProfile: ClassProfile) {
        profileDefaultLessonsCrossoverDao.deleteCrossoversByProfileId(classProfile.id)
    }

    override suspend fun setHomeworkEnabled(profile: ClassProfile, enabled: Boolean) {
        profileDao.setHomeworkEnabledForClassProfile(profile.id, enabled)
    }

    override suspend fun setVppIdForProfile(classProfile: ClassProfile, vppId: VppId.ActiveVppId?) {
        profileDao.setVppIdForClassProfile(classProfile.id, vppId?.id)
    }

    override suspend fun setCalendarIdForProfile(profile: Profile, calendarId: Long?) {
        when (profile) {
            is ClassProfile -> profileDao.setCalendarIdForClassProfile(profile.id, calendarId)
            is TeacherProfile -> profileDao.setCalendarIdForTeacherProfile(profile.id, calendarId)
            is RoomProfile -> profileDao.setCalendarIdForRoomProfile(profile.id, calendarId)
        }
    }

    override suspend fun setCalendarModeForProfile(profile: Profile, calendarMode: ProfileCalendarType) {
        when (profile) {
            is ClassProfile -> profileDao.setCalendarModeForClassProfile(profile.id, calendarMode)
            is TeacherProfile -> profileDao.setCalendarModeForTeacherProfile(profile.id, calendarMode)
            is RoomProfile -> profileDao.setCalendarModeForRoomProfile(profile.id, calendarMode)
        }
    }

    override suspend fun setProfileDisplayName(profile: Profile, displayName: String?) {
        val newName = if (displayName.isNullOrBlank()) profile.originalName else displayName
        when (profile) {
            is ClassProfile -> profileDao.setCustomNameForClassProfile(profile.id, newName)
            is TeacherProfile -> profileDao.setCustomNameForTeacherProfile(profile.id, newName)
            is RoomProfile -> profileDao.setCustomNameForRoomProfile(profile.id, newName)
        }
    }
}
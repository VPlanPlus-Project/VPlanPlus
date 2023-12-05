package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDefaultLessonsCrossoverDao
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val profileDefaultLessonsCrossoverDao: ProfileDefaultLessonsCrossoverDao,
) : ProfileRepository {
    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfiles().map {
            it.map { p ->
                p.toModel()
            }
        }
    }

    override suspend fun getDbProfileById(profileId: UUID): DbProfile? {
        return profileDao.getProfileById(id = profileId).first()?.profile
    }

    override suspend fun createProfile(
        referenceId: UUID,
        type: ProfileType,
        name: String,
        customName: String
    ): UUID {
        val dbProfile = DbProfile(
            referenceId = referenceId,
            type = type,
            name = name,
            customName = customName,
            calendarMode = ProfileCalendarType.NONE,
            calendarId = null
        )
        profileDao.insert(dbProfile)
        return dbProfile.profileId
    }

    override suspend fun getProfileByReferenceId(referenceId: UUID, type: ProfileType): Profile {
        return profileDao.getProfileByReferenceId(referenceId = referenceId, type = type).toModel()
    }

    override fun getProfileById(id: UUID): Flow<Profile?> {
        return profileDao.getProfileById(id = id).map {
            it?.toModel()
        }
    }

    override suspend fun deleteDefaultLessonsFromProfile(profileId: UUID) {
        profileDefaultLessonsCrossoverDao.deleteCrossoversByProfileId(profileId = profileId)
    }

    override suspend fun deleteProfile(profileId: UUID) {
        profileDao.deleteProfile(profileId = profileId)
    }

    override suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile> {
        return profileDao.getProfilesBySchoolId(schoolId = schoolId).map {
            it.toModel()
        }
    }

    override suspend fun updateProfile(profile: DbProfile) {
        profileDao.insert(profile = profile)
    }

    override suspend fun disableDefaultLesson(profileId: UUID, vpId: Long) {
        profileDefaultLessonsCrossoverDao.insertCrossover(
            DbProfileDefaultLesson(
                profileId = profileId,
                defaultLessonVpId = vpId,
                enabled = false
            )
        )
    }

    override suspend fun enableDefaultLesson(profileId: UUID, vpId: Long) {
        profileDefaultLessonsCrossoverDao.insertCrossover(
            DbProfileDefaultLesson(
                profileId = profileId,
                defaultLessonVpId = vpId,
                enabled = true
            )
        )
    }
}
package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.dao.KeyValueDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDefaultLessonsCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val schoolEntityDao: SchoolEntityDao,
    private val keyValueDao: KeyValueDao,
    private val profileDefaultLessonsCrossoverDao: ProfileDefaultLessonsCrossoverDao,
    private val firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository,
) : ProfileRepository {
    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfilesFlow().map {
            it.map { p ->
                p.toModel()
            }
        }
    }

    override suspend fun getDbProfileById(profileId: UUID): DbProfile? {
        return profileDao.getProfileByIdFlow(id = profileId).first()?.profile
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
        if (type == ProfileType.STUDENT) {
            firebaseCloudMessagingManagerRepository.updateToken(null)
        }
        return dbProfile.profileId
    }

    override suspend fun getProfileByReferenceId(referenceId: UUID, type: ProfileType): Profile {
        return profileDao.getProfileByReferenceId(referenceId = referenceId, type = type).toModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getProfileById(id: UUID): Flow<Profile?> {
        return profileDao.getProfileByIdFlow(id = id).mapLatest {
            it?.toModel()
        }
    }

    override suspend fun deleteDefaultLessonsFromProfile(profileId: UUID) {
        profileDefaultLessonsCrossoverDao.deleteCrossoversByProfileId(profileId = profileId)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun deleteProfile(profileId: UUID) {
        val profile = profileDao.getProfileById(id = profileId)!!.toModel()
        profileDao.deleteProfile(profileId = profileId)
        if (profile.type == ProfileType.STUDENT) {
            GlobalScope.launch {
                firebaseCloudMessagingManagerRepository.updateToken(null)
            }
        }
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

    override suspend fun deleteDefaultLessonFromProfile(vpId: Long) {
        profileDefaultLessonsCrossoverDao.deleteCrossoversByDefaultLessonVpId(defaultLessonVpId = vpId)
    }

    override suspend fun getSchoolFromProfile(profile: Profile): School {
        return schoolEntityDao.getSchoolEntityById(profile.referenceId)!!.school
    }

    override suspend fun getActiveProfile() = flow {
        keyValueDao.getFlow(Keys.ACTIVE_PROFILE).collect {
            if (it == null) {
                emit(null)
                return@collect
            }
            val profileId = UUID.fromString(it)
            val profile = profileDao.getProfileById(id = profileId)!!.toModel()
            emit(profile)
        }
    }
}
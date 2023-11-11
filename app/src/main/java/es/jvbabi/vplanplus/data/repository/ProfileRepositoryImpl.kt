package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.xml.ClassBaseData
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.basicAuth
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod.Companion.Get
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao
): ProfileRepository {
    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfiles()
    }

    override suspend fun createProfile(referenceId: Long, type: ProfileType, name: String, customName: String) {
        profileDao.insert(Profile(referenceId = referenceId, type = type, name = name, customName = customName, calendarMode = ProfileCalendarType.NONE))
    }

    override suspend fun getClassesOnline(
        username: String,
        password: String,
        schoolId: Long
    ): List<Classes> {
        val response = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 5000
            }
        }.request("https://www.stundenplan24.de/$schoolId/wplan/wdatenk/SPlanKl_Basis.xml") {
            method = Get
            basicAuth(username, password)
        }
        val baseData = ClassBaseData(response.bodyAsText())
        val classes = ArrayList<Classes>()
        baseData.classes.forEach {
            classes.add(
                Classes(
                    className = it,
                    schoolId = schoolId,
                )
            )
        }
        return classes
    }

    override suspend fun getProfileByReferenceId(referenceId: Long, type: ProfileType): Profile {
        return profileDao.getProfileByReferenceId(referenceId = referenceId, type = type)
    }

    override fun getProfileById(id: Long): Flow<Profile> {
        return profileDao.getProfileById(id = id)
    }

    override suspend fun deleteProfile(profileId: Long) {
        profileDao.deleteProfile(profileId = profileId)
    }

    override suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile> {
        return profileDao.getProfilesBySchoolId(schoolId = schoolId)
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.insert(profile = profile)
    }
}
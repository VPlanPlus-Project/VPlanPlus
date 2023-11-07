package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.ProfileDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
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

    override suspend fun createProfile(referenceId: Long, type: Int, name: String) {
        profileDao.insert(Profile(referenceId = referenceId, type = type, name = name))
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
        val baseData = BaseDataParserStudents(response.bodyAsText())
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

    override suspend fun getProfileByReferenceId(referenceId: Long, type: Int): Profile {
        return profileDao.getProfileByReferenceId(referenceId = referenceId, type = type)
    }

    override suspend fun getProfileById(id: Long): Profile {
        return profileDao.getProfileById(id = id)
    }
}
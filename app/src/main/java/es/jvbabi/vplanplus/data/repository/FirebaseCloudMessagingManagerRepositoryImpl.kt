package es.jvbabi.vplanplus.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdTokenDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FirebaseCloudMessagingManagerRepositoryImpl(
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val profileRepository: ProfileRepository,
    private val groupRepository: GroupRepository,
    private val logRecordRepository: LogRecordRepository,
    private val keyValueRepository: KeyValueRepository,
    private val schoolEntityDao: SchoolEntityDao,
    private val vppIdTokenDao: VppIdTokenDao,
) : FirebaseCloudMessagingManagerRepository {
    override suspend fun updateToken(t: String?): Boolean {
        val token = t ?: FirebaseMessaging.getInstance().token.await()
        logRecordRepository.log("FCM", "Update token $token")

        vppIdNetworkRepository.authentication = null
        logRecordRepository.log("FCM", "Unregister token")
        if (t == null) {
            vppIdNetworkRepository.doRequest(
                path = "/api/${API_VERSION}/firebase",
                requestMethod = HttpMethod.Delete,
                queries = mapOf("token" to token)
            )
        } else if (keyValueRepository.get(Keys.FCM_TOKEN) != null) {
            vppIdNetworkRepository.doRequest(
                path = "/api/${API_VERSION}/firebase",
                requestMethod = HttpMethod.Delete,
                queries = mapOf("token" to keyValueRepository.get(Keys.FCM_TOKEN)!!)
            )
        }

        val profiles = profileRepository
            .getProfiles()
            .first()
            .filterIsInstance<ClassProfile>()

        logRecordRepository.log("FCM", "Building requests for ${profiles.size} profiles")

        val results = mutableListOf<Boolean>()
        profiles.forEach { profile ->
            val vppIdToken = if (profile.vppId != null) vppIdTokenDao.getTokenByVppId(profile.vppId.id)?.accessToken else null
            val schoolAuthentication = profile.group.school.buildAccess().buildVppAuthentication()

            if (vppIdToken == null) vppIdNetworkRepository.authentication = schoolAuthentication
            else vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)

            results.add(
                vppIdNetworkRepository.doRequest(
                    path = "/api/${API_VERSION}/firebase",
                    requestMethod = HttpMethod.Post,
                    requestBody = Gson().toJson(FcmTokenPutRequest(token, profile.displayName)),
                ).response == HttpStatusCode.Created
            )
        }

        return results.all { it }
    }
}

private data class FcmTokenPutRequest(
    @SerializedName("token") val token: String,
    @SerializedName("class_name") val `class`: String
)
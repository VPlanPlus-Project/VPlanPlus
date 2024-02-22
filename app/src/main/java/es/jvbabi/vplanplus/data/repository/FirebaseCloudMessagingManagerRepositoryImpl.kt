package es.jvbabi.vplanplus.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdTokenDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.shared.data.TokenAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.shared.data.VppIdServer
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FirebaseCloudMessagingManagerRepositoryImpl(
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val classRepository: ClassRepository,
    private val logRecordRepository: LogRecordRepository,
    private val profileDao: ProfileDao,
    private val vppIdDao: VppIdDao,
    private val vppIdTokenDao: VppIdTokenDao
) : FirebaseCloudMessagingManagerRepository {
    override suspend fun updateToken(t: String?): Boolean {
        val token = t ?: FirebaseMessaging.getInstance().token.await()
        logRecordRepository.log("FCM", "Update token $token")
        val vppIds = vppIdDao
            .getAll()
            .first()
            .map { it.toModel() }
            .filter { it.isActive() }

        vppIdNetworkRepository.authentication = null
        logRecordRepository.log("FCM", "Unregister token")
        if (t == null) vppIdNetworkRepository.doRequest(
            "/api/${VppIdServer.apiVersion}/fcm/unregister_token/",
            HttpMethod.Delete,
            Gson().toJson(
                FcmTokenPutRequest(
                    token = token
                )
            )
        )

        val profiles = profileDao
            .getProfiles()
            .map { it.toModel() }
            .filter { it.type == ProfileType.STUDENT }

        logRecordRepository.log("FCM", "Building requests for ${profiles.size} profiles")
        val requests = mutableListOf<FcmTokenPutRequestGroup>()
        profiles.forEach { profile ->
            val c = classRepository.getClassById(profile.referenceId)?:return@forEach
            requests.add(
                FcmTokenPutRequestGroup(
                    token = token,
                    vppId = vppIds.firstOrNull { it.classes == c },
                    `class` = c
                )
            )
        }

        val responses = mutableListOf<Boolean>()

        logRecordRepository.log("FCM", "Sending ${requests.size} requests")
        requests.forEach request@{ request ->
            val requestBody: Any = if (request.vppId != null) {
                FcmTokenPutRequest(
                    token = request.token
                )
            } else {
                FcmTokenPutExtendedRequest(
                    token = request.token,
                    `class` = request.`class`.name,
                    school = request.`class`.school.name
                )
            }
            if (request.vppId != null) {
                val vppIdToken = vppIdTokenDao.getTokenByVppId(request.vppId.id)?.token ?: return@request
                vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdToken)
            } else {
                vppIdNetworkRepository.authentication = null
            }

            val response = vppIdNetworkRepository.doRequest(
                "/api/${VppIdServer.apiVersion}/fcm/set_token",
                HttpMethod.Put,
                Gson().toJson(requestBody)
            )
            responses.add(response.response == HttpStatusCode.Created)
        }
        return responses.all { it }
    }
}

private data class FcmTokenPutRequestGroup(
    val token: String,
    val vppId: VppId?,
    val `class`: Classes
)

private data class FcmTokenPutRequest(
    val token: String
)

private data class FcmTokenPutExtendedRequest(
    val token: String,
    val `class`: String,
    val school: String
)
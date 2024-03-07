package es.jvbabi.vplanplus.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdTokenDao
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
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
    private val keyValueRepository: KeyValueRepository,
    private val schoolEntityDao: SchoolEntityDao,
    private val profileDao: ProfileDao,
    private val vppIdDao: VppIdDao,
    private val vppIdTokenDao: VppIdTokenDao,
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
        if (t == null) {
            vppIdNetworkRepository.doRequest(
                path = "/api/${VppIdServer.API_VERSION}/firebase",
                requestMethod = HttpMethod.Delete,
                queries = mapOf("token" to token)
            )
        } else if (keyValueRepository.get(Keys.FCM_TOKEN) != null) {
            vppIdNetworkRepository.doRequest(
                path = "/api/${VppIdServer.API_VERSION}/firebase",
                requestMethod = HttpMethod.Delete,
                queries = mapOf("token" to keyValueRepository.get(Keys.FCM_TOKEN)!!)
            )
        }

        val profiles = profileDao
            .getProfiles()
            .map { it.toModel() }
            .filter { it.type == ProfileType.STUDENT }

        logRecordRepository.log("FCM", "Building requests for ${profiles.size} profiles")

        val results = mutableListOf<Boolean>()
        profiles.forEach { profile ->
            val c = classRepository.getClassById(profile.referenceId)?:return@forEach
            val vppId = vppIds.firstOrNull { it.classes == c }
            val vppIdToken = if (vppId != null) vppIdTokenDao.getTokenByVppId(vppId.id)?.token else null
            val schoolAuthentication = schoolEntityDao.getSchoolEntityById(profile.referenceId)!!.school.buildAuthentication()

            if (vppIdToken == null) vppIdNetworkRepository.authentication = schoolAuthentication
            else vppIdNetworkRepository.authentication = BearerAuthentication(vppIdToken)

            results.add(
                vppIdNetworkRepository.doRequest(
                    path = "/api/${VppIdServer.API_VERSION}/firebase",
                    requestMethod = HttpMethod.Post,
                    requestBody = Gson().toJson(FcmTokenPutRequest(token, c.name)),
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
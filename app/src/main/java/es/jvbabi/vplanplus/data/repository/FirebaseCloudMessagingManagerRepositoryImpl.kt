package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.Gson
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.shared.data.TokenAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.shared.data.VppIdServer
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first

class FirebaseCloudMessagingManagerRepositoryImpl(
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val vppIdRepository: VppIdRepository,
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository
) : FirebaseCloudMessagingManagerRepository {
    override suspend fun updateToken(token: String): Boolean {
        val vppIds = vppIdRepository.getVppIds().first()
        val profiles = profileRepository
            .getProfiles()
            .first()
            .filter { it.type == ProfileType.STUDENT }

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

        requests.forEach request@{ request ->
            val requestBody = if (request.vppId != null) {
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
                val vppIdToken = vppIdRepository.getVppIdToken(request.vppId) ?: return@request
                vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdToken)
            } else {
                vppIdNetworkRepository.authentication = null
            }

            val response = vppIdNetworkRepository.doRequest(
                "/api/${VppIdServer.apiVersion}/fcm/set_token",
                HttpMethod.Put,
                Gson().toJson(requestBody)
            )
            Log.d("FirebaseCloudMessagingManagerRepositoryImpl", "Response: ${response.data} ${Gson().toJson(requestBody)} ${vppIdNetworkRepository.authentication}")
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

private open class FcmTokenPutRequest(
    open val token: String
)

private data class FcmTokenPutExtendedRequest(
    override val token: String,
    val `class`: String,
    val school: String
) : FcmTokenPutRequest(
    token = token
)
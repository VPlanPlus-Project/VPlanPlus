package es.jvbabi.vplanplus.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

class FirebaseCloudMessagingManagerRepositoryImpl(
    private val vppIdNetworkRepository: VppIdNetworkRepository,
) : FirebaseCloudMessagingManagerRepository {

    override suspend fun addTokenUser(user: VppId.ActiveVppId, t: String): Boolean {
        vppIdNetworkRepository.authentication = BearerAuthentication(user.vppIdToken)
        return vppIdNetworkRepository.doRequest(
            path = "/api/${API_VERSION}/user/me/firebase",
            requestMethod = HttpMethod.Post,
            requestBody = Gson().toJson(FcmTokenRequest(t)),
        ).response == HttpStatusCode.Created
    }

    override suspend fun addTokenGroup(group: Group, t: String): Boolean {
        vppIdNetworkRepository.authentication = group.school.buildAccess().buildVppAuthentication()
        return vppIdNetworkRepository.doRequest(
            path = "/api/${API_VERSION}/school/${group.school.id}/group/${group.groupId}/firebase",
            requestMethod = HttpMethod.Post,
            requestBody = Gson().toJson(FcmTokenRequest(t)),
        ).response == HttpStatusCode.Created
    }

    override suspend fun resetToken(t: String): Boolean {
        vppIdNetworkRepository.authentication = null
        return vppIdNetworkRepository.doRequest(
            path = "/api/${API_VERSION}/app/firebase",
            requestMethod = HttpMethod.Delete,
            requestBody = Gson().toJson(FcmTokenRequest(t)),
        ).response == HttpStatusCode.OK
    }
}

private data class FcmTokenRequest(
    @SerializedName("token") val token: String,
)
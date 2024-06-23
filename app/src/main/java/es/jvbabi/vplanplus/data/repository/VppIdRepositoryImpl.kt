package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.DbVppIdToken
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.RoomBookingDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdTokenDao
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.SchoolSp24Access
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.GroupInfoResponse
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdOnlineResponse
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.Session
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import java.util.UUID

class VppIdRepositoryImpl(
    private val vppIdDao: VppIdDao,
    private val vppIdTokenDao: VppIdTokenDao,
    private val groupRepository: GroupRepository,
    private val profileRepository: ProfileRepository,
    private val roomBookingDao: RoomBookingDao,
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
) : VppIdRepository {
    override fun getVppIds(): Flow<List<VppId>> {
        return vppIdDao.getAll().map { list ->
            list.map { it.toModel() }
        }
    }

    override fun getActiveVppIds(): Flow<List<VppId>> {
        return vppIdDao.getAll().map { list ->
            list.filter { it.vppId.state == State.ACTIVE }.map { it.toModel() }
        }
    }

    override suspend fun deleteVppId(id: Int) {
        vppIdDao.delete(id)
    }

    override suspend fun getVppId(id: Int): VppId? {
        return vppIdDao.getVppId(id.toLong())?.toModel()
    }

    override suspend fun addVppId(vppId: VppId) {
        vppIdDao.upsert(
            DbVppId(
                id = vppId.id,
                name = vppId.name,
                schoolId = vppId.schoolId,
                groupName = vppId.groupName,
                classId = groupRepository.getGroupBySchoolAndName(
                    vppId.schoolId,
                    vppId.groupName
                )?.groupId,
                state = State.ACTIVE,
                email = vppId.email,
                cachedAt = ZonedDateTime.now()
            )
        )
    }

    override suspend fun getVppId(id: Long, school: School, forceUpdate: Boolean): VppId? {
        val vppId = vppIdDao.getVppId(id)?.toModel()
        if (vppId != null && vppId.cachedAt.plusHours(6).isAfter(ZonedDateTime.now())) return vppId

        if (vppId?.isActive() == true && getVppIdToken(vppId) != null) {
            vppIdNetworkRepository.authentication = BearerAuthentication(getVppIdToken(vppId)!!)
            val response = vppIdNetworkRepository.doRequest("/api/$API_VERSION/user/me", HttpMethod.Get, null).let {
                if(it.response != HttpStatusCode.OK) return null
                Gson().fromJson(it.data, VppIdOnlineResponse::class.java)
            }?: return null
            vppIdDao.update(
                vppId = vppId.id,
                name = response.username,
                email = response.email,
                cachedAt = ZonedDateTime.now()
            )
            return vppIdDao.getVppId(id)?.toModel()
        }

        val url = "/api/$API_VERSION/user/find/$id"

        vppIdNetworkRepository.authentication = school.buildAccess().buildVppAuthentication()
        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Get,
            null
        )
        if (response.response != HttpStatusCode.OK) return null
        val r = Gson().fromJson(response.data, UserNameResponse::class.java)
        vppIdDao.upsert(
            DbVppId(
                id = id.toInt(),
                name = r.username,
                groupName = r.className,
                schoolId = school.id,
                state = State.CACHE,
                email = null,
                classId = groupRepository.getGroupBySchoolAndName(
                    school.id,
                    r.className
                )?.groupId,
                cachedAt = ZonedDateTime.now()
            )
        )
        return vppIdDao.getVppId(id)?.toModel()
    }

    override suspend fun addVppIdToken(vppId: VppId, token: String, bsToken: String?, initialCreation: Boolean) {
        vppIdTokenDao.insert(
            DbVppIdToken(
                vppId = vppId.id,
                accessToken = token,
                bsToken = bsToken
            )
        )
        if (initialCreation) firebaseCloudMessagingManagerRepository.updateToken(null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
    }

    override suspend fun getVppIdToken(vppId: VppId): String? {
        return vppIdTokenDao.getTokenByVppId(vppId.id)?.accessToken
    }

    override suspend fun getBsToken(vppId: VppId): String? {
        return vppIdTokenDao.getTokenByVppId(vppId.id)?.bsToken
    }

    override suspend fun testVppIdSession(vppId: VppId): Boolean? {
        val currentToken = getVppIdToken(vppId) ?: return null
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me",
            requestMethod = HttpMethod.Get
        )
        if (response.response == null || response.response == HttpStatusCode.BadGateway) return null
        return response.response == HttpStatusCode.OK
    }

    override suspend fun unlinkVppId(vppId: VppId): Boolean {
        val currentToken = getVppIdToken(vppId) ?: return false
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)
        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/auth/logout",
            HttpMethod.Get,
        )
        if (response.response != HttpStatusCode.OK && response.response != HttpStatusCode.Unauthorized) return false

        profileRepository.getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .filter { it.vppId?.id == vppId.id }
            .forEach profileWithThisVppId@{
                profileRepository.setVppIdForProfile(it, null)
            }
        vppIdDao.delete(vppId.id)
        firebaseCloudMessagingManagerRepository.updateToken(null)
        return true
    }

    override suspend fun bookRoom(
        vppId: VppId,
        room: Room,
        from: ZonedDateTime,
        to: ZonedDateTime
    ): BookResult {
        val currentToken = getVppIdToken(vppId) ?: return BookResult.OTHER
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)
        val url = "/api/$API_VERSION/school/${vppId.schoolId}/booking"
        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Post,
            Gson().toJson(
                BookRoomRequest(
                    roomName = room.name,
                    from = ZonedDateTimeConverter().zonedDateTimeToTimestamp(from),
                    to = ZonedDateTimeConverter().zonedDateTimeToTimestamp(to)
                )
            )
        )
        if (response.response != HttpStatusCode.Created) {
            return when (response.response) {
                HttpStatusCode.Conflict -> BookResult.CONFLICT
                else -> BookResult.OTHER
            }
        }
        return BookResult.SUCCESS
    }

    override suspend fun cancelRoomBooking(roomBooking: RoomBooking): HttpStatusCode? {
        val url = "/api/$API_VERSION/school/${roomBooking.bookedBy!!.schoolId}/booking/${roomBooking.id}"
        val currentToken = getVppIdToken(roomBooking.bookedBy) ?: return null
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)

        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Delete,
            null
        )
        if (response.response == HttpStatusCode.OK || response.response == HttpStatusCode.NotFound) roomBookingDao.deleteById(roomBooking.id)

        if (response.response != HttpStatusCode.OK) {
            Log.d("CancelBooking", "status not ok: ${response.response}")
            return response.response
        }

        return response.response
    }

    override suspend fun fetchSessions(vppId: VppId): DataResponse<List<Session>?> {
        val currentToken = getVppIdToken(vppId) ?: return DataResponse(null, HttpStatusCode.Unauthorized)
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)

        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/user/me/session",
            HttpMethod.Get
        )
        return if(response.response != HttpStatusCode.OK || response.data == null) DataResponse(null, response.response)
        else DataResponse(ResponseDataWrapper.fromJson<List<Session>>(response.data), HttpStatusCode.OK)
    }

    override suspend fun closeSession(session: Session, vppId: VppId): Boolean {
        val currentToken = getVppIdToken(vppId) ?: return false
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)

        return try {
            val response = vppIdNetworkRepository.doRequest(
                "/api/$API_VERSION/user/me/session/${session.id}",
                HttpMethod.Delete
            )
            response.response == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun fetchUsersPerClass(sp24Access: SchoolSp24Access): List<GroupInfoResponse>? {
        vppIdNetworkRepository.authentication = sp24Access.buildVppAuthentication()
        val result = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/school/${sp24Access.schoolId}/group/",
            queries = mapOf("only_school_classes" to "true")
        )
        if (result.response != HttpStatusCode.OK || result.data == null) return null
        return ResponseDataWrapper.fromJson<List<GroupInfoResponse>>(result.data)
    }

    override suspend fun getVersionHints(version: Int, versionBefore: Int): DataResponse<List<VersionHints>> {
        vppIdNetworkRepository.authentication = null
        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/app/version_hints/$version",
            HttpMethod.Get,
            queries = mapOf("beforeVersion" to versionBefore.toString())
        )
        if (response.response != HttpStatusCode.OK) return DataResponse(emptyList(), response.response)

        return DataResponse(
            Gson().fromJson(
                response.data,
                VersionHintResponse::class.java
            )
                .data
                .map { it.toModel() },
            response.response
        )
    }

    override suspend fun useOAuthCode(code: String): VppId? {
        vppIdNetworkRepository.authentication = null
        val response = vppIdNetworkRepository.doRequestForm(
            "/api/$API_VERSION/auth/token",
            form = mapOf(
                "grant_type" to "authorization_code",
                "code" to code,
                "client_id" to BuildConfig.VPP_CLIENT_ID,
                "client_secret" to BuildConfig.VPP_CLIENT_SECRET,
                "redirect_uri" to BuildConfig.VPP_REDIRECT_URI
            ),
            requestMethod = HttpMethod.Post
        )
        if (response.response != HttpStatusCode.OK || response.data == null) return null
        val oAuthResponse = Gson().fromJson(response.data, OAuthResponse::class.java)

        vppIdNetworkRepository.authentication = BearerAuthentication(oAuthResponse.accessToken)
        val meResponse = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/user/me",
            HttpMethod.Get
        )

        if (meResponse.response != HttpStatusCode.OK || meResponse.data == null) return null
        val me = ResponseDataWrapper.fromJson<MeResponse>(meResponse.data)

        deleteVppId(me.id)

        val group = groupRepository.getGroupById(me.groupId) ?: return null
        val dbEntity = DbVppId(
            id = me.id,
            name = me.username,
            email = me.email,
            schoolId = group.school.id,
            groupName = group.name,
            classId = group.groupId,
            state = State.ACTIVE,
            cachedAt = ZonedDateTime.now()
        )

        vppIdDao.upsert(dbEntity)

        vppIdTokenDao.insert(DbVppIdToken(
            accessToken = oAuthResponse.accessToken,
            vppId = me.id,
            id = UUID.randomUUID(),
            bsToken = me.schulverwalterAccessToken
        ))
        return getVppId(me.id)
    }
}

private data class OAuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String,
)

private data class MeResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("schulverwalter_access_token") val schulverwalterAccessToken: String,
)

private data class BookRoomRequest(
    @SerializedName("room_name") val roomName: String,
    @SerializedName("from") val from: Long,
    @SerializedName("to") val to: Long,
)

enum class BookResult {
    NO_INTERNET,
    CONFLICT,
    SUCCESS,
    OTHER
}

private data class UserNameResponse(
    @SerializedName("name") val username: String,
    @SerializedName("school_class") val className: String,
)

private data class VersionHintResponse(
    @SerializedName("data") val data: List<VersionHintResponseItem>
)

private data class VersionHintResponseItem(
    @SerializedName("version_code") val version: Int,
    @SerializedName("title") val header: String,
    @SerializedName("content") val content: String,
    @SerializedName("create_at") val createdAt: Long
) {
    fun toModel(): VersionHints {
        return VersionHints(
            header = header,
            content = content,
            version = version,
            createdAt = ZonedDateTimeConverter().timestampToZonedDateTime(createdAt)
        )
    }
}

data class ResponseDataWrapper<T>(
    @SerializedName("data") val data: T
) {
    companion object {
        inline fun <reified T> fromJson(json: String, gson: Gson = Gson()): T {
            val type = object : TypeToken<ResponseDataWrapper<T>>() {}.type
            return (gson.fromJson(json, type) as ResponseDataWrapper<T>).data
        }
    }
}
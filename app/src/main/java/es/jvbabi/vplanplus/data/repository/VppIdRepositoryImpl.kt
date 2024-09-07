package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.data.model.vppid.DbVppId
import es.jvbabi.vplanplus.data.model.vppid.DbVppIdToken
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
import es.jvbabi.vplanplus.domain.model.vpp_id.WebAuthTask
import es.jvbabi.vplanplus.domain.repository.GetWebAuthResponse
import es.jvbabi.vplanplus.domain.repository.GroupInfoResponse
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchulverwalterTokenResponse
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.Session
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.BsNetworkRepository
import es.jvbabi.vplanplus.shared.data.Response
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

class VppIdRepositoryImpl(
    private val vppIdDao: VppIdDao,
    private val vppIdTokenDao: VppIdTokenDao,
    private val groupRepository: GroupRepository,
    private val profileRepository: ProfileRepository,
    private val roomBookingDao: RoomBookingDao,
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val schulverwalterNetworkRepository: BsNetworkRepository
) : VppIdRepository {

    var authTask: WebAuthTask? = null

    override fun getVppIds(): Flow<List<VppId>> {
        return vppIdDao.getAll().map { list ->
            list.map { it.toModel() }
        }
    }

    override fun getActiveVppIds(): Flow<List<VppId.ActiveVppId>> {
        return vppIdDao.getAll().map { list ->
            list.filter { it.vppId.state == State.ACTIVE }.map { it.toModel() as VppId.ActiveVppId }
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

        if (vppId is VppId.ActiveVppId) {
            vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)
            val meResponse = vppIdNetworkRepository.doRequest("/api/$API_VERSION/user/me", HttpMethod.Get)
            if (meResponse.response != HttpStatusCode.OK || meResponse.data == null) return null
            val data = ResponseDataWrapper.fromJson<MeResponse>(meResponse.data)!!
            vppIdDao.update(
                vppId = vppId.id,
                name = data.username,
                email = data.email,
                cachedAt = ZonedDateTime.now()
            )
            return vppIdDao.getVppId(id)?.toModel()
        }

        val url = "/api/$API_VERSION/school/${school.id}/user/find/$id"

        vppIdNetworkRepository.authentication = school.buildAccess().buildVppAuthentication()
        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Get,
            null
        )
        if (response.response != HttpStatusCode.OK || response.data == null) return null
        val r = ResponseDataWrapper.fromJson<UserNameResponse>(response.data)!!
        val group = groupRepository.getGroupById(r.groupId) ?: return null
        vppIdDao.upsert(
            DbVppId(
                id = id.toInt(),
                name = r.username,
                groupName = group.name,
                schoolId = school.id,
                state = State.CACHE,
                email = null,
                classId = group.groupId,
                cachedAt = ZonedDateTime.now()
            )
        )
        return vppIdDao.getVppId(id)?.toModel()
    }

    override suspend fun testVppIdSession(vppId: VppId.ActiveVppId): Boolean? {
        vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/user/me",
            requestMethod = HttpMethod.Get
        )
        if (response.response == null || response.response == HttpStatusCode.BadGateway) return null
        return response.response == HttpStatusCode.OK
    }

    override suspend fun unlinkVppId(vppId: VppId.ActiveVppId): Boolean {
        vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)
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
        return true
    }

    override suspend fun bookRoom(
        vppId: VppId.ActiveVppId,
        room: Room,
        from: ZonedDateTime,
        to: ZonedDateTime
    ): BookResult {
        vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)
        val url = "/api/$API_VERSION/school/${vppId.schoolId}/room/booking"
        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Post,
            Gson().toJson(
                BookRoomRequest(
                    roomId = room.roomId,
                    start = ZonedDateTimeConverter().zonedDateTimeToTimestamp(from),
                    end = ZonedDateTimeConverter().zonedDateTimeToTimestamp(to)
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

    override suspend fun cancelRoomBooking(roomBooking: RoomBooking): Boolean {
        val url = "/api/$API_VERSION/school/${roomBooking.bookedBy!!.schoolId}/room/booking/${roomBooking.id}"
        val vppId = roomBooking.bookedBy as? VppId.ActiveVppId ?: return false
        vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)

        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Delete,
            null
        )
        if (response.response == HttpStatusCode.NoContent || response.response == HttpStatusCode.NotFound) roomBookingDao.deleteById(roomBooking.id)
        else {
            Log.d("CancelBooking", "status not ok: ${response.response}")
            return false
        }

        return true
    }

    override suspend fun fetchSessions(vppId: VppId.ActiveVppId): DataResponse<List<Session>?> {
        vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)

        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/user/me/session",
            HttpMethod.Get
        )
        return if(response.response != HttpStatusCode.OK || response.data == null) DataResponse(null, response.response)
        else DataResponse(ResponseDataWrapper.fromJson<List<Session>>(response.data), HttpStatusCode.OK)
    }

    override suspend fun closeSession(session: Session, vppId: VppId.ActiveVppId): Boolean {
        vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)

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
            "/api/$API_VERSION/school/${sp24Access.schoolId}/group",
            queries = mapOf("only_school_classes" to "true")
        )
        if (result.response != HttpStatusCode.OK || result.data == null) return null
        return ResponseDataWrapper.fromJson<List<GroupInfoResponse>>(result.data)
    }

    override suspend fun getVersionHint(version: Int): DataResponse<VersionHints?> {
        vppIdNetworkRepository.authentication = null
        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/app/version_hints/$version",
            HttpMethod.Get
        )
        if (response.response != HttpStatusCode.OK || response.data == null) return DataResponse(null, response.response)

        return DataResponse(
            ResponseDataWrapper.fromJson<VersionHintResponse>(response.data)!!.toModel(),
            response.response
        )
    }

    override suspend fun testSchulverwalterToken(token: String): Boolean? {
        if (token.isEmpty()) return false
        val LOG_TAG = "VppIdRepository.useOAuthCode"
        schulverwalterNetworkRepository.authentication = BearerAuthentication(token)
        val response = schulverwalterNetworkRepository.doRequest("/api/me")
        if (response.response == HttpStatusCode.Unauthorized) {
            Log.e(LOG_TAG, "Token not valid")
            return false
        }
        if (response.data == null || response.response != HttpStatusCode.OK) {
            Log.e(LOG_TAG, "response not OK, probably no internet")
            return null
        }
        return true
    }

    override suspend fun requestCurrentSchulverwalterToken(vppId: VppId.ActiveVppId): Response<SchulverwalterTokenResponse, String?> {
        val LOG_TAG = "VppIdRepository.requestCurrentSchulverwalterToken"
        vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)
        val meResponse = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/user/me",
            HttpMethod.Get
        )

        if (meResponse.response != HttpStatusCode.OK || meResponse.data == null) {
            Log.e(LOG_TAG, "meResponse not OK")
            return Response(SchulverwalterTokenResponse.NETWORK_ERROR, null)
        }
        val me = ResponseDataWrapper.fromJson<MeResponse>(meResponse.data)!!
        return Response(SchulverwalterTokenResponse.SUCCESS, me.schulverwalterAccessToken)
    }

    override suspend fun setSchulverwalterToken(vppId: VppId.ActiveVppId, token: String) {
        vppIdTokenDao.insert(
            DbVppIdToken(
            accessToken = vppId.vppIdToken,
            vppId = vppId.id,
            bsToken = token
        )
        )
    }

    override suspend fun useOAuthCode(code: String): VppId.ActiveVppId? {
        val LOG_TAG = "VppIdRepository.useOAuthCode"
        vppIdNetworkRepository.authentication = null
        Log.d(LOG_TAG, "using code: ${code.take(8)}${"*".repeat(code.length-8)}")
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
        if (response.response != HttpStatusCode.OK || response.data == null) {
            if (response.data == null) Log.i(LOG_TAG, "response data is null")
            else Log.i(LOG_TAG, "response data: ${response.data}")
            return null
        }
        val oAuthResponse = Gson().fromJson(response.data, OAuthResponse::class.java)

        vppIdNetworkRepository.authentication = BearerAuthentication(oAuthResponse.accessToken)
        val meResponse = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/user/me",
            HttpMethod.Get
        )

        if (meResponse.response != HttpStatusCode.OK || meResponse.data == null) {
            Log.e(LOG_TAG, "meResponse not OK")
            return null
        }
        val me = ResponseDataWrapper.fromJson<MeResponse>(meResponse.data)!!

        deleteVppId(me.id)

        val group = groupRepository.getGroupById(me.groupId) ?: run {
            Log.e(LOG_TAG, "group with id ${me.groupId} not found")
            return null
        }
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

        vppIdTokenDao.insert(
            DbVppIdToken(
            accessToken = oAuthResponse.accessToken,
            vppId = me.id,
            bsToken = me.schulverwalterAccessToken
        )
        )
        return getVppId(me.id) as? VppId.ActiveVppId ?: run {
            Log.w(LOG_TAG, "getVppId returned null")
            null
        }
    }

    override suspend fun getAuthTask(vppId: VppId.ActiveVppId): Response<GetWebAuthResponse, WebAuthTask?> {
        vppIdNetworkRepository.authentication = BearerAuthentication(vppId.vppIdToken)
        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/auth/vpp/task",
            HttpMethod.Get
        )
        if (response.response != HttpStatusCode.OK) return Response(GetWebAuthResponse.ERROR, null)
        val taskResponse: EmojiAuthTaskResponse = ResponseDataWrapper.fromJson<EmojiAuthTaskResponse?>(response.data)
                ?: return Response(GetWebAuthResponse.NO_TASKS, null)

        val task = WebAuthTask(
            taskId = taskResponse.taskId,
            emojis = taskResponse.emojis,
            validUntil = ZonedDateTimeConverter().timestampToZonedDateTime(taskResponse.validUntil),
            vppId = vppId
        )
        authTask = task
        return Response(
            GetWebAuthResponse.TASK_FOUND,
            task
        )
    }

    override suspend fun pickEmoji(task: WebAuthTask, emoji: String): Response<Boolean, Boolean> {
        vppIdNetworkRepository.authentication = BearerAuthentication(task.vppId.vppIdToken)
        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/auth/vpp/task",
            HttpMethod.Post,
            Gson().toJson(mapOf("emoji" to emoji))
        )
        authTask = null
        if (response.response == HttpStatusCode.OK) return Response(code = true, value = true)
        if (response.response == HttpStatusCode.Unauthorized) return Response(code = true, value = false)
        return Response(code = false, value = false)
    }

    override suspend fun getCurrentAuthTask(): Flow<WebAuthTask?> = flow {
        while (true) {
            emit(authTask)
            delay(5000)
        }
    }.distinctUntilChanged()
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
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("start") val start: Long,
    @SerializedName("end") val end: Long,
)

enum class BookResult {
    NO_INTERNET,
    CONFLICT,
    SUCCESS,
    OTHER
}

private data class UserNameResponse(
    @SerializedName("name") val username: String,
    @SerializedName("group_id") val groupId: Int,
)

private data class VersionHintResponse(
    @SerializedName("version_code") val version: Int,
    @SerializedName("title") val header: String,
    @SerializedName("message") val content: String,
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

private data class EmojiAuthTaskResponse(
    @SerializedName("task_id") val taskId: Int,
    @SerializedName("emojis") val emojis: List<String>,
    @SerializedName("valid_until") val validUntil: Long,
)

data class ResponseDataWrapper<T>(
    @SerializedName("data") val data: T
) {
    companion object {
        inline fun <reified T> fromJson(json: String?, gson: Gson = Gson()): T? {
            if (json == null) return null
            val type = object : TypeToken<ResponseDataWrapper<T>>() {}.type
            return (gson.fromJson(json, type) as ResponseDataWrapper<T>).data
        }
    }
}
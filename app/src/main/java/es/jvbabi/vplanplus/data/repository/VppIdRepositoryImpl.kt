package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.DbVppIdToken
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.RoomBookingDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdTokenDao
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.UsersPerClassResponse
import es.jvbabi.vplanplus.domain.repository.VppIdOnlineResponse
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.model.Session
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BasicAuthentication
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

class VppIdRepositoryImpl(
    private val vppIdDao: VppIdDao,
    private val vppIdTokenDao: VppIdTokenDao,
    private val classRepository: ClassRepository,
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

    override suspend fun getVppIdOnline(token: String): DataResponse<VppIdOnlineResponse?> {
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/user/me",
            HttpMethod.Get,
            null
        )
        return if(response.response != HttpStatusCode.OK) {
            DataResponse(
                null, response.response
            )
        } else DataResponse(
            Gson().fromJson(
                response.data,
                VppIdOnlineResponse::class.java
            ), HttpStatusCode.OK
        )
    }

    override suspend fun addVppId(vppId: VppId) {
        vppIdDao.upsert(
            DbVppId(
                id = vppId.id,
                name = vppId.name,
                schoolId = vppId.schoolId,
                className = vppId.className,
                classId = classRepository.getClassBySchoolIdAndClassName(
                    vppId.schoolId,
                    vppId.className
                )?.classId,
                state = State.ACTIVE,
                email = vppId.email,
                cachedAt = ZonedDateTime.now()
            )
        )
    }

    override suspend fun getVppId(id: Int, school: School, forceUpdate: Boolean): VppId? {
        val vppId = vppIdDao.getVppId(id)?.toModel() ?: return null
        if (vppId.cachedAt.plusHours(6).isAfter(ZonedDateTime.now())) return vppId

        if (vppId.isActive() && getVppIdToken(vppId) != null) {
            vppIdNetworkRepository.authentication = BearerAuthentication(getVppIdToken(vppId)!!)
            val url = "/api/$API_VERSION/user/me"
            val response = vppIdNetworkRepository.doRequest(url, HttpMethod.Get, null).let {
                if(it.response != HttpStatusCode.OK) return null
                Gson().fromJson(it.data, VppIdOnlineResponse::class.java)
            }?: return null
            vppIdDao.upsert(
                DbVppId(
                    id = id,
                    name = response.username,
                    className = response.className,
                    schoolId = school.schoolId,
                    state = State.ACTIVE,
                    email = response.email,
                    classId = classRepository.getClassBySchoolIdAndClassName(
                        school.schoolId,
                        response.className
                    )?.classId,
                    cachedAt = ZonedDateTime.now()
                )
            )
            return vppIdDao.getVppId(id)?.toModel()
        }

        val url = "/api/$API_VERSION/user/find/$id"

        vppIdNetworkRepository.authentication = school.buildAuthentication()
        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Get,
            null
        )
        if (response.response != HttpStatusCode.OK) return null
        val r = Gson().fromJson(response.data, UserNameResponse::class.java)
        vppIdDao.upsert(
            DbVppId(
                id = id,
                name = r.username,
                className = r.className,
                schoolId = school.schoolId,
                state = State.CACHE,
                email = null,
                classId = classRepository.getClassBySchoolIdAndClassName(
                    school.schoolId,
                    r.className
                )?.classId,
                cachedAt = ZonedDateTime.now()
            )
        )
        return vppIdDao.getVppId(id)?.toModel()
    }

    override suspend fun addVppIdToken(vppId: VppId, token: String, bsToken: String?, initialCreation: Boolean) {
        vppIdTokenDao.insert(
            DbVppIdToken(
                vppId = vppId.id,
                token = token,
                bsToken = bsToken
            )
        )
        if (initialCreation) firebaseCloudMessagingManagerRepository.updateToken(null)
        vppIdNetworkRepository.authentication = BearerAuthentication(token)
    }

    override suspend fun getVppIdToken(vppId: VppId): String? {
        return vppIdTokenDao.getTokenByVppId(vppId.id)?.token
    }

    override suspend fun getBsToken(vppId: VppId): String? {
        return vppIdTokenDao.getTokenByVppId(vppId.id)?.bsToken
    }

    override suspend fun testVppIdSession(vppId: VppId): Boolean? {
        val currentToken = getVppIdToken(vppId) ?: return null
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)
        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/session",
            requestMethod = HttpMethod.Post,
            requestBody = Gson().toJson(TestSessionRequest(vppId.id))
        )
        if (response.response == null) return null
        return response.response == HttpStatusCode.Found
    }

    override suspend fun unlinkVppId(vppId: VppId): Boolean {
        val currentToken = getVppIdToken(vppId) ?: return false
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)
        val response = vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/session",
            HttpMethod.Delete,
        )
        if (response.response != HttpStatusCode.OK && response.response != HttpStatusCode.Unauthorized) return false
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
            "/api/$API_VERSION/user/me/sessions",
            HttpMethod.Get
        )
        return if(response.response != HttpStatusCode.OK) {
            DataResponse(
                null, response.response
            )
        } else DataResponse(
            Gson().fromJson(
                response.data,
                Array<Session>::class.java
            ).toList(), HttpStatusCode.OK
        )
    }

    override suspend fun closeSession(session: Session, vppId: VppId): Boolean {
        val currentToken = getVppIdToken(vppId) ?: return false
        vppIdNetworkRepository.authentication = BearerAuthentication(currentToken)

        return try {
            val response = vppIdNetworkRepository.doRequest(
                "/api/$API_VERSION/session/${session.id}",
                HttpMethod.Delete
            )
            response.response == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun fetchUsersPerClass(schoolId: Long, username: String, password: String): DataResponse<UsersPerClassResponse?> {
        vppIdNetworkRepository.authentication = BasicAuthentication("$username@$schoolId", password)
        return vppIdNetworkRepository.doRequest(
            "/api/$API_VERSION/school/$schoolId/classes",
        ).let {
            if(it.response != HttpStatusCode.OK) {
                DataResponse(null, it.response)
            } else DataResponse(
                Gson().fromJson(
                    it.data,
                    UsersPerClassResponse::class.java
                ), it.response
            )

        }
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
}

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

private data class TestSessionRequest(
    @SerializedName("user_id") val userId: Int
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
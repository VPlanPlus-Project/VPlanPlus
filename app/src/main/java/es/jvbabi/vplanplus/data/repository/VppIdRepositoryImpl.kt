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
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.VppIdOnlineResponse
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.model.Session
import es.jvbabi.vplanplus.shared.data.TokenAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.shared.data.VppIdServer
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

    override suspend fun getVppIdOnline(token: String): DataResponse<VppIdOnlineResponse?> {
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", token)
        val response = vppIdNetworkRepository.doRequest(
            "/api/${VppIdServer.apiVersion}/vpp_id/user/get_user_details",
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
                email = vppId.email
            )
        )
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
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", token)
    }

    override suspend fun getVppIdToken(vppId: VppId): String? {
        return vppIdTokenDao.getTokenByVppId(vppId.id)?.token
    }

    override suspend fun getBsToken(vppId: VppId): String? {
        return vppIdTokenDao.getTokenByVppId(vppId.id)?.bsToken
    }

    override suspend fun testVppId(vppId: VppId): DataResponse<Boolean?> {
        val currentToken = getVppIdToken(vppId) ?: return DataResponse(false, HttpStatusCode.OK)
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", currentToken)

        val response = vppIdNetworkRepository.doRequest(
            "/api/${VppIdServer.apiVersion}/vpp_id/test_session",
            HttpMethod.Post,
            Gson().toJson(
                TestRequest(
                    id = vppId.id,
                    userName = vppId.name
                )
            )
        )
        return if(response.response != HttpStatusCode.OK) {
            DataResponse(
                null, response.response
            )
        } else DataResponse(
            Gson().fromJson(
                response.data,
                TestResponse::class.java
            ).result, HttpStatusCode.OK
        )
    }

    override suspend fun unlinkVppId(vppId: VppId): Boolean {
        val currentToken = getVppIdToken(vppId) ?: return false
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", currentToken)
        val response = vppIdNetworkRepository.doRequest(
            "/api/${VppIdServer.apiVersion}/vpp_id/unlink_session",
            HttpMethod.Post,
            Gson().toJson(
                TestRequest(
                    id = vppId.id,
                    userName = vppId.name
                )
            )
        )
        if (response.response != HttpStatusCode.OK) return false
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
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", currentToken)
        val url = "/api/${VppIdServer.apiVersion}/vpp_id/booking/book_room"
        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Post,
            Gson().toJson(
                BookRoomRequest(
                    schoolId = room.school.schoolId,
                    roomName = room.name,
                    from = ZonedDateTimeConverter().zonedDateTimeToTimestamp(from),
                    to = ZonedDateTimeConverter().zonedDateTimeToTimestamp(to)
                )
            )
        )
        if (response.response != HttpStatusCode.OK) {
            return when (response.response) {
                HttpStatusCode.Conflict -> BookResult.CONFLICT
                else -> BookResult.OTHER
            }
        }
        return BookResult.SUCCESS
    }

    override suspend fun cacheVppId(id: Int, school: School): VppId? {
        val vppId = vppIdDao.getVppId(id)
        if (vppId != null) return vppId.toModel()
        val url = "/api/${VppIdServer.apiVersion}/vpp_id/user/get_username/$id"

        vppIdNetworkRepository.authentication = TokenAuthentication("sp24.", school.buildToken())
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
            )
        )
        return vppIdDao.getVppId(id)?.toModel()
    }

    override suspend fun cancelRoomBooking(roomBooking: RoomBooking): HttpStatusCode? {
        val url = "/api/${VppIdServer.apiVersion}/vpp_id/booking/cancel_booking/${roomBooking.id}"
        val currentToken = getVppIdToken(roomBooking.bookedBy ?: return null) ?: return null
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", currentToken)

        val response = vppIdNetworkRepository.doRequest(
            url,
            HttpMethod.Post,
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
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", currentToken)

        val response = vppIdNetworkRepository.doRequest(
            "/api/${VppIdServer.apiVersion}/session",
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
                Array<Session>::class.java
            ).toList(), HttpStatusCode.OK
        )
    }

    override suspend fun closeSession(session: Session, vppId: VppId): Boolean {
        val currentToken = getVppIdToken(vppId) ?: return false
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", currentToken)

        return try {
            val response = vppIdNetworkRepository.doRequest(
                "/api/${VppIdServer.apiVersion}/session/${session.id}",
                HttpMethod.Delete
            )
            response.response == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }
}

private data class TestRequest(
    @SerializedName("user_id") val id: Int,
    @SerializedName("name") val userName: String
)

private data class TestResponse(
    val result: Boolean
)

private data class BookRoomRequest(
    @SerializedName("school_id") val schoolId: Long,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("start") val from: Long,
    @SerializedName("end") val to: Long,
)

enum class BookResult {
    NO_INTERNET,
    CONFLICT,
    SUCCESS,
    OTHER
}

private data class UserNameResponse(
    @SerializedName("name") val username: String,
    @SerializedName("class_name") val className: String,
)
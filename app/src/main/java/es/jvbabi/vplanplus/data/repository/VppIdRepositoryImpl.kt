package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.DbVppIdToken
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
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.Response
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.ConnectException
import java.net.UnknownHostException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.jvm.Throws

class VppIdRepositoryImpl(
    private val vppIdDao: VppIdDao,
    private val vppIdTokenDao: VppIdTokenDao,
    private val classRepository: ClassRepository,
    private val roomBookingDao: RoomBookingDao
) : VppIdRepository {
    override fun getVppIds(): Flow<List<VppId>> {
        return vppIdDao.getAll().map { list ->
            list.map { it.toModel() }
        }
    }

    override suspend fun getVppIdOnline(token: String): DataResponse<VppId?> {

        return try {
            val client = createClient()
            val response = client.request {
                headers {
                    set("Authorization", token)
                }
                url {
                    protocol = URLProtocol.HTTPS
                    host = "id.vpp.jvbabi.es"
                    encodedPath = "/api/v1/vpp_id/user/get_user_details"
                }
            }
            client.close()
            if (response.status != HttpStatusCode.OK) {
                DataResponse(
                    null, when (response.status) {
                        HttpStatusCode.Unauthorized -> Response.WRONG_CREDENTIALS
                        HttpStatusCode.NotFound -> Response.NOT_FOUND
                        else -> Response.OTHER
                    }
                )
            } else DataResponse(
                Gson().fromJson(response.bodyAsText(), VppId::class.java),
                Response.SUCCESS
            )
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> return DataResponse(
                    null,
                    Response.NO_INTERNET
                )

                else -> {
                    Log.d(
                        "OnlineRequest",
                        "other error on /api/v1/vpp_id/user/get_user_details: ${e.stackTraceToString()}"
                    )
                    return DataResponse(null, Response.OTHER)
                }
            }

        }
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
                state = State.ACTIVE
            )
        )
    }

    override suspend fun addVppIdToken(vppId: VppId, token: String) {
        vppIdTokenDao.insert(
            DbVppIdToken(
                vppId = vppId.id,
                token = token
            )
        )
    }

    override suspend fun getVppIdToken(vppId: VppId): String? {
        return vppIdTokenDao.getTokenByVppId(vppId.id)?.token
    }

    override suspend fun testVppId(vppId: VppId): DataResponse<Boolean?> {
        val client = createClient()
        val currentToken = getVppIdToken(vppId) ?: return DataResponse(false, Response.SUCCESS)
        return try {
            val response = client.request {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "id.vpp.jvbabi.es"
                    encodedPath = "/api/v1/vpp_id/test_session"
                    method = HttpMethod.Post
                }
                headers {
                    set("Authorization", currentToken)
                }
                setBody(
                    Gson().toJson(
                        TestRequest(
                            id = vppId.id,
                            userName = vppId.name
                        )
                    )
                )
            }
            if (response.status != HttpStatusCode.OK) {
                DataResponse(
                    null, when (response.status) {
                        HttpStatusCode.Unauthorized -> Response.WRONG_CREDENTIALS
                        HttpStatusCode.NotFound -> Response.NOT_FOUND
                        else -> Response.OTHER
                    }
                )
            } else DataResponse(
                Gson().fromJson(
                    response.bodyAsText(),
                    TestResponse::class.java
                ).result, Response.SUCCESS
            )
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> return DataResponse(
                    null,
                    Response.NO_INTERNET
                )

                else -> {
                    Log.d(
                        "OnlineRequest",
                        "other error on /api/v1/vpp_id/test_session: ${e.stackTraceToString()}"
                    )
                    return DataResponse(null, Response.OTHER)
                }
            }

        }
    }

    override suspend fun unlinkVppId(vppId: VppId): Boolean {
        val client = createClient()
        val currentToken = getVppIdToken(vppId) ?: return false
        return try {
            val response = client.request {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "id.vpp.jvbabi.es"
                    encodedPath = "/api/v1/vpp_id/unlink_session"
                    method = HttpMethod.Post
                }
                headers {
                    set("Authorization", currentToken)
                }
                setBody(
                    Gson().toJson(
                        TestRequest(
                            id = vppId.id,
                            userName = vppId.name
                        )
                    )
                )
            }
            client.close()
            if (response.status != HttpStatusCode.OK) return false
            vppIdDao.delete(vppId.id)
            true
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> false

                else -> {
                    Log.d(
                        "OnlineRequest",
                        "other error on /api/v1/vpp_id/test_session: ${e.stackTraceToString()}"
                    )
                    return false
                }
            }
        }
    }

    companion object {
        fun createClient(): HttpClient {
            return HttpClient(Android) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 5000
                }
                install(UserAgent) {
                    agent = "VPlanPlus"
                }
            }
        }

        @Throws(
            UnknownHostException::class,
            ConnectTimeoutException::class,
            HttpRequestTimeoutException::class,
            ConnectException::class
        )
        suspend fun executeRequest(
            url: String,
            token: String,
            method: HttpMethod = HttpMethod.Get,
            body: String? = null
        ): HttpResponse {
            val client = createClient()
            val response = client.request {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "id.vpp.jvbabi.es"
                    encodedPath = url
                    this@request.method = method
                }
                headers {
                    set("Authorization", token)
                }
                if (body != null) setBody(body)
            }
            client.close()
            return response
        }
    }

    override suspend fun bookRoom(
        vppId: VppId,
        room: Room,
        from: LocalDateTime,
        to: LocalDateTime
    ): BookResult {
        val currentToken = getVppIdToken(vppId) ?: return BookResult.OTHER
        val zoneOffset = ZoneId
            .systemDefault().rules
            .getOffset(
                Instant.now()
            )
        val url = "/api/v1/vpp_id/booking/book_room"
        return try {
            val response = executeRequest(
                url,
                currentToken,
                HttpMethod.Post,
                Gson().toJson(
                    BookRoomRequest(
                        schoolId = room.school.schoolId,
                        roomName = room.name,
                        from = from.toEpochSecond(zoneOffset),
                        to = to.toEpochSecond(zoneOffset)
                    )
                )
            )
            if (response.status != HttpStatusCode.OK) {
                return when (response.status) {
                    HttpStatusCode.Conflict -> BookResult.CONFLICT
                    else -> BookResult.OTHER
                }
            }
            BookResult.SUCCESS
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> BookResult.NO_INTERNET
                else -> {
                    Log.d(
                        "OnlineRequest",
                        "other error on $url: ${e.stackTraceToString()}"
                    )
                    return BookResult.OTHER
                }
            }
        }
    }

    override suspend fun cacheVppId(id: Int, school: School): VppId? {
        val vppId = vppIdDao.getVppId(id)
        if (vppId != null) return vppId.toModel()
        val url = "/api/v1/vpp_id/user/get_username/$id"
        return try {
            val response = executeRequest(
                url,
                school.buildToken()
            )
            if (response.status != HttpStatusCode.OK) return null
            val r = Gson().fromJson(response.bodyAsText(), UserNameResponse::class.java)
            vppIdDao.upsert(
                DbVppId(
                    id = id,
                    name = r.username,
                    className = r.className,
                    schoolId = school.schoolId,
                    state = State.CACHE,
                    classId = classRepository.getClassBySchoolIdAndClassName(
                        school.schoolId,
                        r.className
                    )?.classId,
                )
            )
            vppIdDao.getVppId(id)?.toModel()
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> null
                else -> {
                    Log.d(
                        "OnlineRequest",
                        "other error on $url: ${e.stackTraceToString()}"
                    )
                    return null
                }
            }
        }
    }

    override suspend fun cancelRoomBooking(roomBooking: RoomBooking): HttpStatusCode? {
        val url = "/api/v1/vpp_id/booking/cancel_booking/${roomBooking.id}"
        val currentToken = getVppIdToken(roomBooking.bookedBy ?: return null) ?: return null
        return try {
            val response = executeRequest(
                url,
                currentToken,
                HttpMethod.Post
            )
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NotFound) roomBookingDao.deleteById(
                roomBooking.id
            )
            if (response.status != HttpStatusCode.OK) {
                Log.d("CancelBooking", "status not ok: ${response.status}")
                return response.status
            }
            response.status
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> null
                else -> {
                    Log.d(
                        "OnlineRequest",
                        "other error on $url: ${e.stackTraceToString()}"
                    )
                    return null
                }
            }
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
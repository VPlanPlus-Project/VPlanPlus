package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.Gson
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.DbVppIdToken
import es.jvbabi.vplanplus.data.source.database.dao.VppIdDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdTokenDao
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.ConnectException
import java.net.UnknownHostException

class VppIdRepositoryImpl(
    private val vppIdDao: VppIdDao,
    private val vppIdTokenDao: VppIdTokenDao,
    private val classRepository: ClassRepository
) : VppIdRepository {
    override fun getVppIds(): Flow<List<VppId>> {
        return vppIdDao.getAll().map { list ->
            list.map { it.toModel() }
        }
    }

    override suspend fun getVppIdOnline(token: String): DataResponse<VppId?> {
        val client = HttpClient(Android) {
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 5000
            }
            install(UserAgent) {
                agent = "VPlanPlus"
            }
        }

        return try {
            val response = client.request {
                headers {
                    set("Authorization", token)
                }
                url {
                    protocol = URLProtocol.HTTPS
                    host = "id.vpp.jvbabi.es"
                    encodedPath = "/api/v1/vpp_id/get_user_details"
                }
            }
            if (response.status != HttpStatusCode.OK) {
                DataResponse(
                    null, when (response.status) {
                        HttpStatusCode.Unauthorized -> Response.WRONG_CREDENTIALS
                        HttpStatusCode.NotFound -> Response.NOT_FOUND
                        else -> Response.OTHER
                    }
                )
            } else DataResponse(Gson().fromJson(response.bodyAsText(), VppId::class.java), Response.SUCCESS)
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> return DataResponse(
                    null,
                    Response.NO_INTERNET
                )

                else -> {
                    Log.d("OnlineRequest", "other error on /api/v1/vpp_id/get_user_details: ${e.stackTraceToString()}")
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
                classId = classRepository.getClassBySchoolIdAndClassName(vppId.schoolId, vppId.className)?.classId,
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
}
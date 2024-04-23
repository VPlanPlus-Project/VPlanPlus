package es.jvbabi.vplanplus.domain.repository

import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.Session
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface VppIdRepository {
    fun getVppIds(): Flow<List<VppId>>
    fun getActiveVppIds(): Flow<List<VppId>>
    suspend fun getVppIdOnline(token: String): DataResponse<VppIdOnlineResponse?>

    suspend fun addVppId(vppId: VppId)

    suspend fun getVppId(id: Long, school: School, forceUpdate: Boolean): VppId?

    suspend fun addVppIdToken(vppId: VppId, token: String, bsToken: String?, initialCreation: Boolean)
    suspend fun getVppIdToken(vppId: VppId): String?
    suspend fun getBsToken(vppId: VppId): String?

    suspend fun testVppIdSession(vppId: VppId): Boolean?
    suspend fun unlinkVppId(vppId: VppId): Boolean

    suspend fun bookRoom(vppId: VppId, room: Room, from: ZonedDateTime, to: ZonedDateTime): BookResult
    suspend fun cancelRoomBooking(roomBooking: RoomBooking): HttpStatusCode?

    suspend fun fetchSessions(vppId: VppId): DataResponse<List<Session>?>
    suspend fun closeSession(session: Session, vppId: VppId): Boolean

    suspend fun fetchUsersPerClass(schoolId: Long, username: String, password: String): DataResponse<UsersPerClassResponse?>

    suspend fun getVersionHints(version: Int, versionBefore: Int): DataResponse<List<VersionHints>>
}

data class VppIdOnlineResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("sp24_school_id") val schoolId: Long,
    @SerializedName("class_name") val className: String,
    @SerializedName("bs_token") val bsToken: String?
)

data class UsersPerClassResponse(
    @SerializedName("data") val classes: List<UsersPerClassResponseRecord>
)

data class UsersPerClassResponseRecord(
    @SerializedName("class_name") val className: String,
    @SerializedName("students_count") val users: Int
)
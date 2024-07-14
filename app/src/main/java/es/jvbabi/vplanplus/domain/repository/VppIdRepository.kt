package es.jvbabi.vplanplus.domain.repository

import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.SchoolSp24Access
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.Session
import es.jvbabi.vplanplus.shared.data.Response
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface VppIdRepository {
    fun getVppIds(): Flow<List<VppId>>
    fun getActiveVppIds(): Flow<List<VppId.ActiveVppId>>
    suspend fun getVppId(id: Int): VppId?

    suspend fun deleteVppId(id: Int)

    suspend fun addVppId(vppId: VppId)

    suspend fun getVppId(id: Long, school: School, forceUpdate: Boolean): VppId?

    suspend fun addVppIdToken(vppId: VppId, token: String, bsToken: String?, initialCreation: Boolean)
    suspend fun testVppIdSession(vppId: VppId.ActiveVppId): Boolean?

    suspend fun unlinkVppId(vppId: VppId.ActiveVppId): Boolean
    suspend fun bookRoom(vppId: VppId.ActiveVppId, room: Room, from: ZonedDateTime, to: ZonedDateTime): BookResult

    suspend fun cancelRoomBooking(roomBooking: RoomBooking): Boolean
    suspend fun fetchSessions(vppId: VppId.ActiveVppId): DataResponse<List<Session>?>

    suspend fun closeSession(session: Session, vppId: VppId.ActiveVppId): Boolean
    suspend fun useOAuthCode(code: String): VppId.ActiveVppId?

    /**
     * @return A map with the class name as key and the number of students in that class as value or null if something went wrong
     */
    suspend fun fetchUsersPerClass(sp24Access: SchoolSp24Access): List<GroupInfoResponse>?

    suspend fun getVersionHint(version: Int): DataResponse<VersionHints?>

    /**
     * Tests if the given token is valid using the Schulverwalter API.
     * @param token The token to test
     * @return true if the token is valid, false if it is not valid, null if something went wrong
     */
    suspend fun testSchulverwalterToken(token: String): Boolean?

    suspend fun requestCurrentSchulverwalterToken(vppId: VppId.ActiveVppId): Response<SchulverwalterTokenResponse, String?>
    suspend fun setSchulverwalterToken(vppId: VppId.ActiveVppId, token: String)
}

data class GroupInfoResponse(
    @SerializedName("group_name") val className: String,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("members") val users: Int
)

enum class SchulverwalterTokenResponse {
    SUCCESS,
    NO_TOKENS,
    NETWORK_ERROR
}
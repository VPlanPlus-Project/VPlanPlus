package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VppId
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface VppIdRepository {
    fun getVppIds(): Flow<List<VppId>>
    suspend fun getVppIdOnline(token: String): DataResponse<VppIdOnlineResponse?>

    /**
     * If this id is already cached, it will return the vpp.ID, otherwise it will fetch it from the server, cache it and return its username
     * @param id the id of the user
     * @param school the school where the user is expected to be
     * @return the now cached vpp.ID or null if something went wrong
     */
    suspend fun cacheVppId(id: Int, school: School): VppId?
    suspend fun addVppId(vppId: VppId)

    suspend fun addVppIdToken(vppId: VppId, token: String, bsToken: String?)
    suspend fun getVppIdToken(vppId: VppId): String?
    suspend fun getBsToken(vppId: VppId): String?

    suspend fun testVppId(vppId: VppId): DataResponse<Boolean?>
    suspend fun unlinkVppId(vppId: VppId): Boolean

    suspend fun bookRoom(vppId: VppId, room: Room, from: LocalDateTime, to: LocalDateTime): BookResult
    suspend fun cancelRoomBooking(roomBooking: RoomBooking): HttpStatusCode?
}

data class VppIdOnlineResponse(
    val id: VppId,
    val bsToken: String?
)
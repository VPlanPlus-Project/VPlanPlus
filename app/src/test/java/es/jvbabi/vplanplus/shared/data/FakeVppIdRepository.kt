package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.data.model.DbVppIdToken
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.UsersPerClassResponse
import es.jvbabi.vplanplus.domain.repository.VppIdOnlineResponse
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.Session
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.ZonedDateTime

class FakeVppIdRepository : VppIdRepository {

    private val vppIds = mutableListOf<VppId>()
    private val tokens = mutableListOf<DbVppIdToken>()
    override fun getVppIds(): Flow<List<VppId>> {
        return flow { emit(vppIds) }
    }

    override fun getActiveVppIds(): Flow<List<VppId>> {
        TODO("Not yet implemented")
    }

    override suspend fun getVppIdOnline(token: String): DataResponse<VppIdOnlineResponse?> {
        TODO("Not yet implemented")
    }

    override suspend fun addVppId(vppId: VppId) {
        vppIds.add(vppId)
    }

    override suspend fun getVppId(id: Long, school: School, forceUpdate: Boolean): VppId? {
        TODO("Not yet implemented")
    }

    override suspend fun addVppIdToken(
        vppId: VppId,
        token: String,
        bsToken: String?,
        initialCreation: Boolean
    ) {
        tokens.removeIf { it.vppId == vppId.id }
        if (initialCreation) tokens.add(
            DbVppIdToken(
                vppId = vppId.id,
                token = token,
                bsToken = bsToken
            )
        )
    }

    override suspend fun getVppIdToken(vppId: VppId): String? {
        return tokens.firstOrNull { it.vppId == vppId.id }?.token
    }

    override suspend fun getBsToken(vppId: VppId): String? {
        return tokens.firstOrNull { it.vppId == vppId.id }?.bsToken
    }

    override suspend fun testVppIdSession(vppId: VppId): Boolean? {
        TODO("Not yet implemented")
    }

    override suspend fun unlinkVppId(vppId: VppId): Boolean {
        vppIds.removeIf { it == vppId }
        return true
    }

    override suspend fun bookRoom(
        vppId: VppId,
        room: Room,
        from: ZonedDateTime,
        to: ZonedDateTime
    ): BookResult {
        TODO("Not yet implemented")
    }

    override suspend fun cancelRoomBooking(roomBooking: RoomBooking): HttpStatusCode? {
        TODO("Not yet implemented")
    }

    override suspend fun fetchSessions(vppId: VppId): DataResponse<List<Session>?> {
        TODO("Not yet implemented")
    }

    override suspend fun closeSession(session: Session, vppId: VppId): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun fetchUsersPerClass(schoolId: Long, username: String, password: String): DataResponse<UsersPerClassResponse?> {
        TODO("Not yet implemented")
    }

    override suspend fun getVersionHints(version: Int, versionBefore: Int): DataResponse<List<VersionHints>> {
        TODO("Not yet implemented")
    }
}
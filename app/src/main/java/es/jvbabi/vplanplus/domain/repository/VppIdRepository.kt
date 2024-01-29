package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.VppId
import kotlinx.coroutines.flow.Flow

interface VppIdRepository {
    fun getVppIds(): Flow<List<VppId>>
    suspend fun getVppIdOnline(token: String): DataResponse<VppId?>
    suspend fun addVppId(vppId: VppId)

    suspend fun addVppIdToken(vppId: VppId, token: String)
    suspend fun getVppIdToken(vppId: VppId): String?

    suspend fun testVppId(vppId: VppId): DataResponse<Boolean?>
    suspend fun unlinkVppId(vppId: VppId): Boolean
}
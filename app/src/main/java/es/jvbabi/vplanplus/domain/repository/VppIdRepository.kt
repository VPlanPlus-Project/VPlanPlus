package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.VppId
import kotlinx.coroutines.flow.Flow

interface VppIdRepository {
    fun getVppIds(): Flow<List<VppId>>
    suspend fun addVppId(vppId: VppId)
}
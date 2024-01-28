package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.VppIdDao
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VppIdRepositoryImpl(
    private val vppIdDao: VppIdDao
) : VppIdRepository {
    override fun getVppIds(): Flow<List<VppId>> {
        return vppIdDao.getAll().map { list ->
            list.map { it.toModel() }
        }
    }

    override suspend fun addVppId(vppId: VppId) {
        TODO("Not yet implemented")
    }
}
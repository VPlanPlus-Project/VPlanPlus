package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.XmlBaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository

class BaseDataUseCases(
    private val baseDataRepository: BaseDataRepository,
) {

    suspend fun getBaseData(schoolId: Long, username: String, password: String): DataResponse<XmlBaseData?> {
        return baseDataRepository.getFullBaseData(schoolId, username, password)
    }

    suspend fun processBaseData(schoolId: Long, baseData: XmlBaseData) {
        baseDataRepository.processBaseData(schoolId, baseData)
    }
}
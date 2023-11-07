package es.jvbabi.vplanplus.domain.usecase
// TODO: refactor other use cases to use this one

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.BaseData
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository

class BaseDataUseCases(
    private val baseDataRepository: BaseDataRepository,
    private val weekRepository: WeekRepository
) {

    suspend fun getBaseDataXml(schoolId: Long, username: String, password: String): OnlineResponse<BaseData?> {
        return baseDataRepository.getBaseData(schoolId, username, password)
    }

    suspend fun insertWeeks(weeks: List<Week>) = weekRepository.insertWeeks(weeks)

    suspend fun processBaseData(schoolId: Long, baseData: BaseData) {
        baseDataRepository.processBaseData(schoolId, baseData)
    }
}
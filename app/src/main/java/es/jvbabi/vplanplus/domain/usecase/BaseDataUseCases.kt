package es.jvbabi.vplanplus.domain.usecase
// TODO: refactor other use cases to use this one

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository

class BaseDataUseCases(
    private val baseDataRepository: BaseDataRepository
) {

    suspend fun getBaseDataXml(schoolId: String, username: String, password: String): OnlineResponse<BaseDataParserStudents?> {
        return baseDataRepository.getBaseData(schoolId, username, password)
    }
}
package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents

interface BaseDataRepository {

    suspend fun getBaseData(
        schoolId: String,
        username: String,
        password: String,
    ): OnlineResponse<BaseDataParserStudents?>
}
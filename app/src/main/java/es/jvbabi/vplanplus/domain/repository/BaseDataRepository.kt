package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.BaseData
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
import es.jvbabi.vplanplus.domain.model.xml.WeekData

interface BaseDataRepository {

    suspend fun getBaseData(
        schoolId: String,
        username: String,
        password: String,
    ): OnlineResponse<BaseData?>

    suspend fun processBaseData(schoolId: String, baseData: BaseData) {
        processBaseDataStudents(baseData.students)
        processBaseDataWeeks(schoolId, baseData.weekData)
    }

    suspend fun processBaseDataStudents(baseDataParserStudents: BaseDataParserStudents)
    suspend fun processBaseDataWeeks(schoolId: String, weekData: WeekData)

}
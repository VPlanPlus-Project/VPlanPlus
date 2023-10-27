package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.BaseData
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
import es.jvbabi.vplanplus.domain.model.xml.WeekData

interface BaseDataRepository {

    suspend fun getBaseData(
        schoolId: Long,
        username: String,
        password: String,
    ): OnlineResponse<BaseData?>

    suspend fun processBaseData(schoolId: Long, baseData: BaseData) {
        processBaseDataStudents(baseData.students)
        processBaseDataWeeks(schoolId, baseData.weekData)
    }

    suspend fun processBaseDataStudents(baseDataParserStudents: BaseDataParserStudents)
    suspend fun processBaseDataWeeks(schoolId: Long, weekData: WeekData)

}
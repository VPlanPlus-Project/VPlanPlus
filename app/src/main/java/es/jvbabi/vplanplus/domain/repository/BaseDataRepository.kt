package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.XmlBaseData

interface BaseDataRepository {

    suspend fun processBaseData(schoolId: Long, baseData: XmlBaseData)

    suspend fun getFullBaseData(
        schoolId: Long,
        username: String,
        password: String,
    ): DataResponse<XmlBaseData?>

    suspend fun checkCredentials(
        schoolId: Long,
        username: String,
        password: String,
    ): DataResponse<Boolean?>

}
package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.XmlBaseData

interface BaseDataRepository {

    suspend fun processBaseData(schoolId: Int, baseData: XmlBaseData)

    suspend fun getFullBaseData(
        sp24SchoolId: Int,
        username: String,
        password: String,
    ): DataResponse<XmlBaseData?>

    suspend fun checkCredentials(
        schoolId: Int,
        username: String,
        password: String,
    ): DataResponse<Boolean?>

}
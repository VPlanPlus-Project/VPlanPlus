package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.Response
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.shared.domain.repository.NetworkRepository
import java.time.LocalDate
import kotlin.io.encoding.ExperimentalEncodingApi

class VPlanRepositoryImpl(
    private val networkRepository: NetworkRepository
) : VPlanRepository {
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getVPlanData(school: School, date: LocalDate): DataResponse<VPlanData?> {
        networkRepository.authentication = BasicAuthentication(school.username, school.password)
        val response = networkRepository.doRequest(
            path = "/${school.schoolId}/mobil/mobdaten/PlanKl${date.year}${date.monthValue.toString().padStart(2, '0')}${date.dayOfMonth.toString().padStart(2, '0')}.xml",
        )
        if (response.response == Response.NOT_FOUND) return DataResponse(null, Response.NO_DATA_AVAILABLE)
        if (response.data == null) return DataResponse(null, response.response)
        return DataResponse(VPlanData(schoolId = school.schoolId, xml = response.data), response.response)
    }
}
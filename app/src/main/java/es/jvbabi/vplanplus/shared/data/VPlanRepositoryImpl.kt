package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.shared.domain.repository.NetworkRepository
import io.ktor.http.HttpStatusCode
import java.time.LocalDate

class VPlanRepositoryImpl(
    private val networkRepository: NetworkRepository
) : VPlanRepository {
    override suspend fun getVPlanData(sp24SchoolId: Int, username: String, password: String, date: LocalDate): DataResponse<VPlanData?> {
        networkRepository.authentication = BasicAuthentication(username, password)
        val response = networkRepository.doRequest(
            path = "/${sp24SchoolId}/mobil/mobdaten/PlanKl${date.year}${date.monthValue.toString().padStart(2, '0')}${date.dayOfMonth.toString().padStart(2, '0')}.xml",
        )
        if (response.response == HttpStatusCode.NotFound) return DataResponse(null, HttpStatusCode.NotFound)
        if (response.data == null || response.response != HttpStatusCode.OK) return DataResponse(null, response.response)
        return DataResponse(VPlanData(sp24SchoolId = sp24SchoolId, xml = response.data), response.response)
    }
}
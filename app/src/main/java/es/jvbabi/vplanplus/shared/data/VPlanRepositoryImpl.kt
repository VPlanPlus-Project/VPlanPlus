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
        val responseWplan = networkRepository.doRequest(
            path = "/${sp24SchoolId}/wplan/wdatenk/WPlanKl_${date.year}${date.monthValue.toString().padStart(2, '0')}${date.dayOfMonth.toString().padStart(2, '0')}.xml",
        )
        if (responseWplan.response == HttpStatusCode.NotFound) {
            val responeMobdata = networkRepository.doRequest(
                path = "/${sp24SchoolId}/mobil/mobdaten/PlanKl${date.year}${date.monthValue.toString().padStart(2, '0')}${date.dayOfMonth.toString().padStart(2, '0')}.xml",
            )
            if (responeMobdata.response == HttpStatusCode.NotFound) return DataResponse(null, HttpStatusCode.NotFound)
            if (responeMobdata.data == null || responeMobdata.response != HttpStatusCode.OK) return DataResponse(null, responeMobdata.response)
            return DataResponse(VPlanData(sp24SchoolId = sp24SchoolId, xml = responeMobdata.data), responeMobdata.response)
        }
        if (responseWplan.response == HttpStatusCode.NotFound) return DataResponse(null, HttpStatusCode.NotFound)
        if (responseWplan.data == null || responseWplan.response != HttpStatusCode.OK) return DataResponse(null, responseWplan.response)
        return DataResponse(VPlanData(sp24SchoolId = sp24SchoolId, xml = responseWplan.data), responseWplan.response)
    }
}
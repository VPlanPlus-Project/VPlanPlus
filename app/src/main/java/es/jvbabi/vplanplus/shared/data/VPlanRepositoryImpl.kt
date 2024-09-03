package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.model.xml.SPlanData
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.model.xml.WPlanSPlan
import es.jvbabi.vplanplus.domain.model.xml.WPlanSPlanData
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.shared.domain.repository.NetworkRepository
import io.ktor.http.HttpStatusCode
import java.time.LocalDate

class VPlanRepositoryImpl(
    private val networkRepository: NetworkRepository
) : VPlanRepository {
    override suspend fun getVPlanData(
        sp24SchoolId: Int,
        username: String,
        password: String,
        date: LocalDate,
        preferredDownloadMode: SchoolDownloadMode
    ): DataResponse<VPlanData?> {
        networkRepository.authentication = BasicAuthentication(username, password)
        val data = when (preferredDownloadMode) {
            SchoolDownloadMode.INDIWARE_WOCHENPLAN_6 -> getDataFromWplan(sp24SchoolId, date)
            SchoolDownloadMode.INDIWARE_MOBIL -> getDataFromMobdata(sp24SchoolId, date)
        }
        if (data.data == null || data.response == HttpStatusCode.NotFound) {
            return when (preferredDownloadMode) {
                SchoolDownloadMode.INDIWARE_WOCHENPLAN_6 -> getDataFromMobdata(sp24SchoolId, date)
                SchoolDownloadMode.INDIWARE_MOBIL -> getDataFromWplan(sp24SchoolId, date)
            }
        }
        return data
    }

    private suspend fun getDataFromWplan(
        sp24SchoolId: Int,
        date: LocalDate,
    ): DataResponse<VPlanData?> {
        val response = networkRepository.doRequest(
            path = "/${sp24SchoolId}/wplan/wdatenk/WPlanKl_${date.year}${date.monthValue.toString().padStart(2, '0')}${date.dayOfMonth.toString().padStart(2, '0')}.xml",
        )
        if (response.data == null || response.response != HttpStatusCode.OK) return DataResponse(null, response.response)
        return DataResponse(VPlanData(sp24SchoolId = sp24SchoolId, xml = response.data), response.response)
    }

    private suspend fun getDataFromMobdata(
        sp24SchoolId: Int,
        date: LocalDate,
    ): DataResponse<VPlanData?> {
        val response = networkRepository.doRequest(
            path = "/${sp24SchoolId}/mobil/mobdaten/PlanKl${date.year}${date.monthValue.toString().padStart(2, '0')}${date.dayOfMonth.toString().padStart(2, '0')}.xml",
        )
        if (response.data == null || response.response != HttpStatusCode.OK) return DataResponse(null, response.response)
        return DataResponse(VPlanData(sp24SchoolId = sp24SchoolId, xml = response.data), response.response)
    }

    override suspend fun getSPlanDataViaWPlan6(sp24SchoolId: Int, username: String, password: String, weekNumber: Int): DataResponse<WPlanSPlan?> {
        networkRepository.authentication = BasicAuthentication(username, password)
        val response = networkRepository.doRequest(
            path = "/${sp24SchoolId}/wplan/wdatenk/SPlanKl_Sw${weekNumber}.xml",
        )
        if (response.data == null || response.response != HttpStatusCode.OK) return DataResponse(null, response.response)
        return DataResponse(WPlanSPlanData(response.data).sPlan, response.response)
    }

    override suspend fun getSPlanData(sp24SchoolId: Int, username: String, password: String): DataResponse<SPlanData?> {
        networkRepository.authentication = BasicAuthentication(username, password)
        val response = networkRepository.doRequest(
            path = "/${sp24SchoolId}/splan/sdaten/splank.xml",
        )
        if (response.data == null || response.response != HttpStatusCode.OK) return DataResponse(null, response.response)
        return DataResponse(SPlanData(xml = response.data), response.response)
    }
}
package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import java.time.LocalDate

interface VPlanRepository {
    suspend fun getVPlanData(sp24SchoolId: Int, username: String, password: String, date: LocalDate): DataResponse<VPlanData?>
}
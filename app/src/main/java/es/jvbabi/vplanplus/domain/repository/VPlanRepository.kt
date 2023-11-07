package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import java.time.LocalDate

interface VPlanRepository {
    suspend fun getVPlanData(school: School, date: LocalDate): OnlineResponse<VPlanData?>
}
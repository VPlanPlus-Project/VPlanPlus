package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.model.xml.SPlanData
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.model.xml.WPlanSPlan
import java.time.LocalDate

interface VPlanRepository {
    suspend fun getVPlanData(sp24SchoolId: Int, username: String, password: String, date: LocalDate, preferredDownloadMode: SchoolDownloadMode): DataResponse<VPlanData?>
    suspend fun getSPlanDataViaWPlan6(sp24SchoolId: Int, username: String, password: String, weekNumber: Int): DataResponse<WPlanSPlan?>
    suspend fun getSPlanData(sp24SchoolId: Int, username: String, password: String): DataResponse<SPlanData?>
}
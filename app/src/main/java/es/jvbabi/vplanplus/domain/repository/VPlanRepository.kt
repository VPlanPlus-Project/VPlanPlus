package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.model.xml.SPlanData
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import java.time.LocalDate

interface VPlanRepository {
    suspend fun getVPlanData(sp24SchoolId: Int, username: String, password: String, date: LocalDate, preferredDownloadMode: SchoolDownloadMode): DataResponse<VPlanData?>
    suspend fun getSPlanData(sp24SchoolId: Int, username: String, password: String): DataResponse<SPlanData?>
}
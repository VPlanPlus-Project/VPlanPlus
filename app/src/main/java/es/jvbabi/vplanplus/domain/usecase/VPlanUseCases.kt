package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import java.time.LocalDate

class VPlanUseCases(
    private val vPlanRepository: VPlanRepository
) {
    suspend fun getVPlanData(school: School, date: LocalDate): OnlineResponse<VPlanData?> {
        return vPlanRepository.getVPlanData(school, date)
    }

}
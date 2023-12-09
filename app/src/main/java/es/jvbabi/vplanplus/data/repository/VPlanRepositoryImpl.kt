package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.online.OnlineRequest
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import java.time.LocalDate

class VPlanRepositoryImpl(
    private val logRecordRepository: LogRecordRepository
) : VPlanRepository {
    override suspend fun getVPlanData(school: School, date: LocalDate): DataResponse<VPlanData?> {

        val response = OnlineRequest(logRecordRepository).getResponse(
            "https://www.stundenplan24.de/${school.schoolId}/mobil/mobdaten/PlanKl${date.year}${date.monthValue}${date.dayOfMonth.toString().padStart(2, '0')}.xml",
            school.username, school.password
        )
        if (response.response == Response.NOT_FOUND) return DataResponse(null, Response.NO_DATA_AVAILABLE)
        if (response.data == null) return DataResponse(null, response.response)
        return DataResponse(VPlanData(schoolId = school.schoolId, xml = response.data), response.response)
    }
}
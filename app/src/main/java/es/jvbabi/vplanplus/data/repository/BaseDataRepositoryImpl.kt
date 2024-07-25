package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.XmlBaseData
import es.jvbabi.vplanplus.domain.model.xml.ClassBaseData
import es.jvbabi.vplanplus.domain.model.xml.RoomBaseData
import es.jvbabi.vplanplus.domain.model.xml.TeacherBaseData
import es.jvbabi.vplanplus.domain.model.xml.WeekBaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.shared.data.BasicAuthentication
import es.jvbabi.vplanplus.shared.data.Sp24NetworkRepository
import io.ktor.http.HttpStatusCode
import java.time.LocalDate

class BaseDataRepositoryImpl(
    private val sp24NetworkRepository: Sp24NetworkRepository
) : BaseDataRepository {

    override suspend fun getFullBaseData(
        sp24SchoolId: Int,
        username: String,
        password: String
    ): DataResponse<XmlBaseData?> {
        sp24NetworkRepository.authentication = BasicAuthentication(username, password)
        val classesResponse = sp24NetworkRepository.doRequest(
            "/$sp24SchoolId/wplan/wdatenk/SPlanKl_Basis.xml"
        )
        val teachersResponse = sp24NetworkRepository.doRequest(
            "/$sp24SchoolId/wplan/wdatenl/SPlanLe_Basis.xml",
        )
        val roomsResponse = sp24NetworkRepository.doRequest(
            "/$sp24SchoolId/wplan/wdatenr/SPlanRa_Basis.xml",
        )
        val weeksResponse = sp24NetworkRepository.doRequest(
            "/$sp24SchoolId/wplan/wdatenk/SPlanKl_Sw1.xml",
        )
        if (classesResponse.response != HttpStatusCode.OK || arrayOf(classesResponse.data, weeksResponse.data).any { it == null }) return DataResponse(null, classesResponse.response)

        val fullySupported = teachersResponse.response == HttpStatusCode.OK && roomsResponse.response == HttpStatusCode.OK && weeksResponse.response == HttpStatusCode.OK

        val classBaseData = ClassBaseData(classesResponse.data!!)
        val teacherBaseData = if (fullySupported && teachersResponse.data != null) TeacherBaseData(teachersResponse.data) else null
        val roomBaseData = if (fullySupported && roomsResponse.data != null) RoomBaseData(roomsResponse.data) else null
        val weekBaseData = WeekBaseData(weeksResponse.data!!)

        return DataResponse(
            XmlBaseData(
                classBaseData.classes,
                teacherBaseData?.teacherShorts,
                roomBaseData?.roomNames,
                classBaseData.schoolName,
                classBaseData.daysPerWeek,
                classBaseData.holidays.map {
                    Holiday(
                        date = LocalDate.of(it.first.first, it.first.second, it.first.third),
                        schoolId = if (it.second) null else sp24SchoolId.toLong()
                    )
                },
                weekBaseData.times
            ),
            HttpStatusCode.OK
        )
    }

    override suspend fun checkCredentials(schoolId: Int, username: String, password: String): DataResponse<Boolean?> {
        sp24NetworkRepository.authentication = BasicAuthentication(username, password)
        val response = sp24NetworkRepository.doRequest("/$schoolId/wplan/wdatenk/SPlanKl_Basis.xml")
        return DataResponse(response.response == HttpStatusCode.OK, response.response)
    }
}
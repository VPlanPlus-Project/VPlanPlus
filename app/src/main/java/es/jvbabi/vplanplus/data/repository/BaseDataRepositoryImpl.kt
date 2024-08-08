package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.model.xml.ClassBaseData
import es.jvbabi.vplanplus.domain.model.xml.MobileBaseData
import es.jvbabi.vplanplus.domain.model.xml.RoomBaseData
import es.jvbabi.vplanplus.domain.model.xml.TeacherBaseData
import es.jvbabi.vplanplus.domain.model.xml.WeekBaseData
import es.jvbabi.vplanplus.domain.repository.BaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataClass
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.BaseDataResponse
import es.jvbabi.vplanplus.shared.data.BasicAuthentication
import es.jvbabi.vplanplus.shared.data.Sp24NetworkRepository
import io.ktor.http.HttpStatusCode
import java.time.LocalDate

class BaseDataRepositoryImpl(
    private val sp24NetworkRepository: Sp24NetworkRepository
) : BaseDataRepository {

    override suspend fun checkCredentials(
        schoolId: Int,
        username: String,
        password: String
    ): DataResponse<Boolean?> {
        sp24NetworkRepository.authentication = BasicAuthentication(username, password)
        val response = sp24NetworkRepository.doRequest("/$schoolId/wplan/wdatenk/SPlanKl_Basis.xml")
        return DataResponse(response.response == HttpStatusCode.OK, response.response)
    }

    override suspend fun getBaseData(
        sp24SchoolId: Int,
        username: String,
        password: String
    ): BaseDataResponse {
        sp24NetworkRepository.authentication = BasicAuthentication(username, password)
        val wplanClassResponse =
            sp24NetworkRepository.doRequest("/$sp24SchoolId/wplan/wdatenk/SPlanKl_Basis.xml")
        if (wplanClassResponse.response == null) return BaseDataResponse.Error
        val wplanSw1Response = sp24NetworkRepository.doRequest("/$sp24SchoolId/wplan/wdatenk/SPlanKl_Sw1.xml")
        if (wplanClassResponse.response == HttpStatusCode.NotFound || wplanSw1Response.response == HttpStatusCode.NotFound) return getBaseDataUsingMobileData(
            sp24SchoolId,
            username,
            password
        )
        if (wplanClassResponse.response== HttpStatusCode.Unauthorized) return BaseDataResponse.Unauthorized
        if (wplanClassResponse.response != HttpStatusCode.OK || wplanClassResponse.data == null || wplanSw1Response.data == null) return BaseDataResponse.Error

        val classBaseData = ClassBaseData(wplanClassResponse.data)
        val weekData = WeekBaseData(wplanSw1Response.data)

        val wplanTeacherResponse =
            sp24NetworkRepository.doRequest("/$sp24SchoolId/wplan/wdatenl/SPlanLe_Basis.xml")
        val teacherBaseData =
            if (wplanTeacherResponse.response == HttpStatusCode.OK && wplanTeacherResponse.data != null) TeacherBaseData(
                wplanTeacherResponse.data
            ) else null

        val wplanRoomResponse =
            sp24NetworkRepository.doRequest("/$sp24SchoolId/wplan/wdatenr/SPlanRa_Basis.xml")
        val roomBaseData =
            if (wplanRoomResponse.response == HttpStatusCode.OK && wplanRoomResponse.data != null) RoomBaseData(
                wplanRoomResponse.data
            ) else null

        return BaseDataResponse.Success(BaseData(
            classes = classBaseData.classes.map { group ->
                BaseDataClass(
                    name = group.name,
                    lessonTimes = weekData.times.filterKeys { it == group.name }.values.firstOrNull() ?: emptyMap()
                )
            },
            teachers = teacherBaseData?.teacherShorts,
            rooms = roomBaseData?.roomNames,
            downloadMode = SchoolDownloadMode.INDIWARE_WOCHENPLAN_6,
            daysPerWeek = classBaseData.daysPerWeek,
            holidays = classBaseData.holidays.map { LocalDate.of(it.first.first, it.first.second, it.first.third) }
        ))
    }

    private suspend fun getBaseDataUsingMobileData(
        sp24SchoolId: Int,
        username: String,
        password: String
    ): BaseDataResponse {
        sp24NetworkRepository.authentication = BasicAuthentication(username, password)
        val mobileResponse = sp24NetworkRepository.doRequest("/$sp24SchoolId/mobil/mobdaten/Klassen.xml")
        if (mobileResponse.response == HttpStatusCode.Unauthorized) return BaseDataResponse.Unauthorized
        if (mobileResponse.response != HttpStatusCode.OK || mobileResponse.data == null) return BaseDataResponse.Error
        return BaseDataResponse.Success(MobileBaseData(mobileResponse.data).baseData)
    }
}
package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.model.WPlanBaseData
import es.jvbabi.vplanplus.domain.model.xml.ClassBaseData
import es.jvbabi.vplanplus.domain.model.xml.MobileBaseData
import es.jvbabi.vplanplus.domain.model.xml.RoomBaseData
import es.jvbabi.vplanplus.domain.model.xml.TeacherBaseData
import es.jvbabi.vplanplus.domain.model.xml.WeekBaseData
import es.jvbabi.vplanplus.domain.repository.BaseData
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
    ): DataResponse<WPlanBaseData?> {
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
        if (classesResponse.response != HttpStatusCode.OK || arrayOf(
                classesResponse.data,
                weeksResponse.data
            ).any { it == null }
        ) return DataResponse(null, classesResponse.response)

        val fullySupported =
            teachersResponse.response == HttpStatusCode.OK && roomsResponse.response == HttpStatusCode.OK && weeksResponse.response == HttpStatusCode.OK

        val classBaseData = ClassBaseData(classesResponse.data!!)
        val teacherBaseData =
            if (fullySupported && teachersResponse.data != null) TeacherBaseData(teachersResponse.data) else null
        val roomBaseData =
            if (fullySupported && roomsResponse.data != null) RoomBaseData(roomsResponse.data) else null
        val weekBaseData = WeekBaseData(weeksResponse.data!!)

        return DataResponse(
            WPlanBaseData(
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
    ): BaseData? {
        sp24NetworkRepository.authentication = BasicAuthentication(username, password)
        val wplanClassResponse =
            sp24NetworkRepository.doRequest("/$sp24SchoolId/wplan/wdatenk/SPlanKl_Basis.xml")
        if (wplanClassResponse.response == null) return null
        if (wplanClassResponse.response == HttpStatusCode.NotFound) return getBaseDataUsingMobileData(
            sp24SchoolId,
            username,
            password
        )
        if (wplanClassResponse.response != HttpStatusCode.OK) return null

        val classBaseData = ClassBaseData(wplanClassResponse.data!!)

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

        return BaseData(
            classes = TODO(),
            teachers = teacherBaseData?.teacherShorts,
            rooms = roomBaseData?.roomNames,
            downloadMode = SchoolDownloadMode.INDIWARE_WOCHENPLAN_6,
            daysPerWeek = classBaseData.daysPerWeek,
            holidays = classBaseData.holidays.map { LocalDate.of(it.first.first, it.first.second, it.first.third) }
        )
    }

    private suspend fun getBaseDataUsingMobileData(
        sp24SchoolId: Int,
        username: String,
        password: String
    ): BaseData? {
        sp24NetworkRepository.authentication = BasicAuthentication(username, password)
        val mobileResponse = sp24NetworkRepository.doRequest("/$sp24SchoolId/mobil/mobdaten/Klassen.xml")
        if (mobileResponse.response != HttpStatusCode.OK || mobileResponse.data == null) return null
        return MobileBaseData(mobileResponse.data).baseData
    }
}
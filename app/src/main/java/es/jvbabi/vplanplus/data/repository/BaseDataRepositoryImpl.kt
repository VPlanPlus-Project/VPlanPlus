package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.XmlBaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.model.xml.ClassBaseData
import es.jvbabi.vplanplus.domain.model.xml.RoomBaseData
import es.jvbabi.vplanplus.domain.model.xml.TeacherBaseData
import es.jvbabi.vplanplus.domain.model.xml.WeekBaseData
import es.jvbabi.vplanplus.shared.data.BasicAuthentication
import es.jvbabi.vplanplus.shared.data.Sp24NetworkRepository
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.toZonedDateTime
import io.ktor.http.HttpStatusCode
import java.time.LocalDate

class BaseDataRepositoryImpl(
    private val groupRepository: GroupRepository,
    private val lessonTimeRepository: LessonTimeRepository,
    private val holidayRepository: HolidayRepository,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository,
    private val sp24NetworkRepository: Sp24NetworkRepository
) : BaseDataRepository {

    override suspend fun processBaseData(schoolId: Int, baseData: XmlBaseData) {
        groupRepository.deleteGroupsBySchoolId(schoolId)
        /*groupRepository.insertGroup(schoolId, baseData.classNames)*/ // TODO
        /*holidayRepository.replaceHolidays(baseData.holidays)*/ // TODO
        baseData.lessonTimes.forEach { entry ->
            val `class` = groupRepository.getGroupBySchoolAndName(schoolId, entry.key)!!
            lessonTimeRepository.deleteLessonTimes(`class`)
            entry.value.forEach { lessonTime ->
                val from = "${lessonTime.value.first}:00".toZonedDateTime().atBeginningOfTheWorld()
                val to = "${lessonTime.value.second}:00".toZonedDateTime().atBeginningOfTheWorld()
                lessonTimeRepository.insertLessonTime(
                    LessonTime(
                        groupId = `class`.groupId,
                        lessonNumber = lessonTime.key,
                        from = (from.hour * 60L * 60L) + (from.minute * 60L),
                        to = (to.hour * 60L * 60L) + (to.minute * 60L)
                    )
                )
            }
        }
        if (baseData.roomNames != null) {
            roomRepository.deleteRoomsBySchoolId(schoolId)
            roomRepository.insertRoomsByName(schoolId, baseData.roomNames)
        }

        if (baseData.teacherShorts != null) {
            teacherRepository.deleteTeachersBySchoolId(schoolId)
            teacherRepository.insertTeachersByAcronym(schoolId, baseData.teacherShorts)
        }
    }

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
        if (classesResponse.response != HttpStatusCode.OK) return DataResponse(null, classesResponse.response)

        val fullySupported = teachersResponse.response == HttpStatusCode.OK && roomsResponse.response == HttpStatusCode.OK && weeksResponse.response == HttpStatusCode.OK

        val classBaseData = ClassBaseData(classesResponse.data!!)
        val teacherBaseData = if (fullySupported) TeacherBaseData(teachersResponse.data!!) else null
        val roomBaseData = if (fullySupported) RoomBaseData(roomsResponse.data!!) else null
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
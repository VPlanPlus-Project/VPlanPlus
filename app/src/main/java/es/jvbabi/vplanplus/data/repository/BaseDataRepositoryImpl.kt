package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.XmlBaseData
import es.jvbabi.vplanplus.feature.onboarding.domain.model.xml.ClassBaseData
import es.jvbabi.vplanplus.feature.onboarding.domain.model.xml.RoomBaseData
import es.jvbabi.vplanplus.feature.onboarding.domain.model.xml.TeacherBaseData
import es.jvbabi.vplanplus.feature.onboarding.domain.model.xml.WeekBaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.shared.data.BasicAuthentication
import es.jvbabi.vplanplus.shared.data.Sp24NetworkRepository
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.toLocalDateTime
import io.ktor.http.HttpStatusCode
import java.time.LocalDate
import kotlin.io.encoding.ExperimentalEncodingApi

class BaseDataRepositoryImpl(
    private val classRepository: ClassRepository,
    private val lessonTimeRepository: LessonTimeRepository,
    private val holidayRepository: HolidayRepository,
    private val weekRepository: WeekRepository,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository,
    private val sp24NetworkRepository: Sp24NetworkRepository
) : BaseDataRepository {

    override suspend fun processBaseData(schoolId: Long, baseData: XmlBaseData) {
        classRepository.deleteClassesBySchoolId(schoolId)
        classRepository.insertClasses(schoolId, baseData.classNames)
        holidayRepository.replaceHolidays(baseData.holidays)
        weekRepository.replaceWeeks(baseData.weeks.map { it.toWeek(schoolId) })
        baseData.lessonTimes.forEach { entry ->
            val `class` = classRepository.getClassBySchoolIdAndClassName(schoolId, entry.key)!!
            lessonTimeRepository.deleteLessonTimes(`class`)
            entry.value.forEach { lessonTime ->
                lessonTimeRepository.insertLessonTime(
                    LessonTime(
                        classLessonTimeRefId = `class`.classId,
                        lessonNumber = lessonTime.key,
                        start = "${lessonTime.value.first}:00".toLocalDateTime().atBeginningOfTheWorld(),
                        end = "${lessonTime.value.second}:00".toLocalDateTime().atBeginningOfTheWorld(),
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

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getFullBaseData(
        schoolId: Long,
        username: String,
        password: String
    ): DataResponse<XmlBaseData?> {
        sp24NetworkRepository.authentication = BasicAuthentication(username, password)
        val classesResponse = sp24NetworkRepository.doRequest(
            "/$schoolId/wplan/wdatenk/SPlanKl_Basis.xml"
        )
        val teachersResponse = sp24NetworkRepository.doRequest(
            "/$schoolId/wplan/wdatenl/SPlanLe_Basis.xml",
        )
        val roomsResponse = sp24NetworkRepository.doRequest(
            "/$schoolId/wplan/wdatenr/SPlanRa_Basis.xml",
        )
        val weeksResponse = sp24NetworkRepository.doRequest(
            "/$schoolId/wplan/wdatenk/SPlanKl_Sw1.xml",
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
                        schoolHolidayRefId = if (it.second) null else schoolId
                    )
                },
                classBaseData.schoolWeeks,
                weekBaseData.times
            ),
            HttpStatusCode.OK
        )
    }
}
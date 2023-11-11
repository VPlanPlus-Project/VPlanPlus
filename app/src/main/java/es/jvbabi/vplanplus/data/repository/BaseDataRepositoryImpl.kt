package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.online.OnlineRequest
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.XmlBaseData
import es.jvbabi.vplanplus.domain.model.xml.ClassBaseData
import es.jvbabi.vplanplus.domain.model.xml.RoomBaseData
import es.jvbabi.vplanplus.domain.model.xml.TeacherBaseData
import es.jvbabi.vplanplus.domain.model.xml.WeekBaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import java.time.LocalDate

class BaseDataRepositoryImpl(
    private val classRepository: ClassRepository,
    private val lessonTimeRepository: LessonTimeRepository,
    private val holidayRepository: HolidayRepository,
    private val weekRepository: WeekRepository,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository,
    private val logRecordRepository: LogRecordRepository
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
                        classId = `class`.id!!,
                        lessonNumber = lessonTime.key,
                        start = lessonTime.value.first,
                        end = lessonTime.value.second
                    )
                )
            }
        }
        roomRepository.deleteRoomsBySchoolId(schoolId)
        roomRepository.insertRoomsByName(schoolId, baseData.roomNames)

        teacherRepository.deleteTeachersBySchoolId(schoolId)
        teacherRepository.insertTeachersByAcronym(schoolId, baseData.teacherShorts)
    }

    override suspend fun getFullBaseData(
        schoolId: Long,
        username: String,
        password: String
    ): DataResponse<XmlBaseData?> {
        val onlineRequest = OnlineRequest(logRecordRepository)
        val classesResponse = onlineRequest.getResponse(
            "https://www.stundenplan24.de/$schoolId/wplan/wdatenk/SPlanKl_Basis.xml",
            username,
            password
        )
        val teachersResponse = onlineRequest.getResponse(
            "https://www.stundenplan24.de/$schoolId/wplan/wdatenl/SPlanLe_Basis.xml",
            username,
            password
        )
        val roomsResponse = onlineRequest.getResponse(
            "https://www.stundenplan24.de/$schoolId/wplan/wdatenr/SPlanRa_Basis.xml",
            username,
            password
        )
        val weeksResponse = onlineRequest.getResponse(
            "https://www.stundenplan24.de/$schoolId/wplan/wdatenk/SPlanKl_Sw1.xml",
            username,
            password
        )
        if (classesResponse.response != Response.SUCCESS || teachersResponse.response != Response.SUCCESS || roomsResponse.response != Response.SUCCESS || weeksResponse.response != Response.SUCCESS) {
            return DataResponse(null, Response.OTHER)
        }
        val classBaseData = ClassBaseData(classesResponse.data!!)
        val teacherBaseData = TeacherBaseData(teachersResponse.data!!)
        val roomBaseData = RoomBaseData(roomsResponse.data!!)
        val weekBaseData = WeekBaseData(weeksResponse.data!!)

        return DataResponse(
            XmlBaseData(
                classBaseData.classes,
                teacherBaseData.teacherShorts,
                roomBaseData.roomNames,
                classBaseData.schoolName,
                classBaseData.daysPerWeek,
                classBaseData.holidays.map {
                    Holiday(
                        date = LocalDate.of(it.first.first, it.first.second, it.first.third),
                        schoolId = if (it.second) null else schoolId
                    )
                },
                classBaseData.schoolWeeks,
                weekBaseData.times
            ),
            Response.SUCCESS
        )
    }
}
package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.Response
import es.jvbabi.vplanplus.domain.model.XmlBaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import java.time.format.DateTimeFormatter
import java.util.UUID

class FakeBaseDataRepository : BaseDataRepository {
    override suspend fun processBaseData(schoolId: Long, baseData: XmlBaseData) {}

    override suspend fun getFullBaseData(
        schoolId: Long,
        username: String,
        password: String
    ): DataResponse<XmlBaseData?> {
        val school = FakeSchoolRepository.exampleSchools.firstOrNull { it.schoolId == schoolId }
        return DataResponse(
            data = XmlBaseData(
                classNames = FakeClassRepository.classNames,
                holidays = FakeHolidayRepository.holidaysForSchool(schoolId),
                weeks = emptyList(),
                lessonTimes = FakeClassRepository.classNames.associateWith {
                    FakeLessonTimesRepository
                        .lessonTimesForClass(UUID.randomUUID()).associate {
                            it.lessonNumber to (it.start.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            ) to it.end.format(DateTimeFormatter.ofPattern("HH:mm")))
                        }
                },
                roomNames = FakeRoomRepository.roomNames,
                teacherShorts = FakeTeacherRepository.teacherNames,
                daysPerWeek = 5,
                schoolName = school?.name?:"This is a fake school",
            ),
            response = Response.SUCCESS
        )
    }
}
package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.XmlBaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import io.ktor.http.HttpStatusCode
import java.time.format.DateTimeFormatter
import java.util.UUID

class FakeBaseDataRepository(private val fullySupportedSchool: Map<Long, Boolean>) : BaseDataRepository {
    override suspend fun processBaseData(schoolId: Long, baseData: XmlBaseData) {}

    override suspend fun getFullBaseData(
        schoolId: Long,
        username: String,
        password: String
    ): DataResponse<XmlBaseData?> {
        val school =
            FakeSchoolRepository.exampleSchools.firstOrNull { it.schoolId == schoolId }?.copy(
                fullyCompatible = fullySupportedSchool[schoolId] ?: false
            )
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
                roomNames = if (fullySupportedSchool[schoolId] == true) FakeRoomRepository.roomNames else emptyList(),
                teacherShorts = if (fullySupportedSchool[schoolId] == true) FakeTeacherRepository.teacherNames else emptyList(),
                daysPerWeek = 5,
                schoolName = school?.name ?: "This is a fake school",
            ),
            response = HttpStatusCode.OK
        )
    }
}
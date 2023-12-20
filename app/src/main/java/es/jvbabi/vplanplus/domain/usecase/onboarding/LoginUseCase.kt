package es.jvbabi.vplanplus.domain.usecase.onboarding

import com.google.gson.Gson
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.usecase.Response

class LoginUseCase(
    private val schoolRepository: SchoolRepository,
    private val kv: KeyValueRepository,
    private val baseDataRepository: BaseDataRepository
) {

    suspend operator fun invoke(schoolId: String, username: String, password: String): Response {
        if (schoolRepository.getSchoolFromId(schoolId.toLong()) != null) return Response.SUCCESS // already logged in

        val gson = Gson()

        val baseDataResponse = baseDataRepository.getFullBaseData(schoolId.toLong(), username, password)
        if (baseDataResponse.response != Response.SUCCESS) return baseDataResponse.response
        val lessonTimes = baseDataResponse.data!!.lessonTimes.map { `class` ->
            `class`.value.map { lessonTime ->
                LessonTime(
                    className = `class`.key,
                    lessonNumber = lessonTime.key,
                    startTime = lessonTime.value.first,
                    endTime = lessonTime.value.second
                )
            }
        }.flatten()

        kv.set("onboarding.school.$schoolId.classes", baseDataResponse.data.classNames.joinToString(","))
        kv.set("onboarding.school.$schoolId.teachers", baseDataResponse.data.teacherShorts.joinToString(","))
        kv.set("onboarding.school.$schoolId.rooms", baseDataResponse.data.roomNames.joinToString(","))
        kv.set("onboarding.school.$schoolId.name", baseDataResponse.data.schoolName)
        kv.set("onboarding.school.$schoolId.daysPerWeek", baseDataResponse.data.daysPerWeek.toString())
        kv.set("onboarding.school.$schoolId.holidays", baseDataResponse.data.holidays.joinToString(",") { it.date.toString() })
        kv.set("onboarding.school.$schoolId.lessonTimes", gson.toJson(lessonTimes))

        return Response.SUCCESS
    }
}

data class LessonTime(
    val className: String,
    val lessonNumber: Int,
    val startTime: String,
    val endTime: String
)
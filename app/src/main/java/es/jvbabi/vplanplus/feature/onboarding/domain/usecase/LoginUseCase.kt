package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.gson.Gson
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.Response
import es.jvbabi.vplanplus.feature.onboarding.ui.LoginState

class LoginUseCase(
    private val schoolRepository: SchoolRepository,
    private val kv: KeyValueRepository,
    private val baseDataRepository: BaseDataRepository
) {

    suspend operator fun invoke(schoolId: String, username: String, password: String): LoginResult {
        val existingSchool = schoolRepository.getSchoolFromId(schoolId.toLong())
        if (existingSchool != null) return if (existingSchool.fullyCompatible) LoginResult.SUCCESS else LoginResult.PARTIAL_SUCCESS // already logged in

        val gson = Gson()

        val baseDataResponse = baseDataRepository.getFullBaseData(schoolId.toLong(), username, password)
        if (baseDataResponse.response != Response.SUCCESS) return when (baseDataResponse.response) {
            Response.WRONG_CREDENTIALS -> LoginResult.WRONG_CREDENTIALS
            Response.NO_INTERNET -> LoginResult.NO_INTERNET
            else -> LoginResult.PARTIAL_SUCCESS
        }
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
        kv.set("onboarding.school.$schoolId.teachers", baseDataResponse.data.teacherShorts?.joinToString(",")?:"")
        kv.set("onboarding.school.$schoolId.rooms", baseDataResponse.data.roomNames?.joinToString(",")?:"")
        kv.set("onboarding.school.$schoolId.name", baseDataResponse.data.schoolName)
        kv.set("onboarding.school.$schoolId.daysPerWeek", baseDataResponse.data.daysPerWeek.toString())
        kv.set("onboarding.school.$schoolId.holidays", baseDataResponse.data.holidays.joinToString(",") { it.date.toString() })
        kv.set("onboarding.school.$schoolId.lessonTimes", gson.toJson(lessonTimes))

        return if (baseDataResponse.data.teacherShorts == null || baseDataResponse.data.roomNames == null) LoginResult.PARTIAL_SUCCESS else LoginResult.SUCCESS
    }
}

data class LessonTime(
    val className: String,
    val lessonNumber: Int,
    val startTime: String,
    val endTime: String
)

enum class LoginResult {
    SUCCESS,
    WRONG_CREDENTIALS,
    NO_INTERNET,
    PARTIAL_SUCCESS,
}

fun LoginResult.toResponse(): Response {
    return when (this) {
        LoginResult.SUCCESS -> Response.SUCCESS
        LoginResult.WRONG_CREDENTIALS -> Response.WRONG_CREDENTIALS
        LoginResult.NO_INTERNET -> Response.NO_INTERNET
        LoginResult.PARTIAL_SUCCESS -> Response.SUCCESS
    }
}

fun LoginResult.toLoginState(): LoginState {
    return when (this) {
        LoginResult.SUCCESS -> LoginState.FULL
        LoginResult.PARTIAL_SUCCESS -> LoginState.PARTIAL
        else -> LoginState.NONE
    }
}
package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.gson.Gson
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.onboarding.ui.LoginState
import io.ktor.http.HttpStatusCode

/**
 * Fetch the base data if school is new to local database and store its base-data to key value store.
 * If the school is already in the local database, return the login state.
 */
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
        if (baseDataResponse.response != HttpStatusCode.OK) return when (baseDataResponse.response) {
            HttpStatusCode.Forbidden, HttpStatusCode.Unauthorized -> LoginResult.WRONG_CREDENTIALS
            null -> LoginResult.NO_INTERNET
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

fun LoginResult.toResponse(): HttpStatusCode? {
    return when (this) {
        LoginResult.SUCCESS -> HttpStatusCode.OK
        LoginResult.WRONG_CREDENTIALS -> HttpStatusCode.Forbidden
        LoginResult.NO_INTERNET -> null
        LoginResult.PARTIAL_SUCCESS -> HttpStatusCode.OK
    }
}

fun LoginResult.toLoginState(): LoginState {
    return when (this) {
        LoginResult.SUCCESS -> LoginState.FULL
        LoginResult.PARTIAL_SUCCESS -> LoginState.PARTIAL
        else -> LoginState.NONE
    }
}
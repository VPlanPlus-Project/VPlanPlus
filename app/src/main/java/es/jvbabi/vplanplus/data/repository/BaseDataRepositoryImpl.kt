package es.jvbabi.vplanplus.data.repository

import android.util.Log
import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.BaseData
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
import es.jvbabi.vplanplus.domain.model.xml.WeekData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.basicAuth
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import java.net.ConnectException
import java.net.UnknownHostException

class BaseDataRepositoryImpl(
    private val classRepository: ClassRepository,
    private val lessonTimeRepository: LessonTimeRepository,
) : BaseDataRepository {
    override suspend fun getBaseData(
        schoolId: Long,
        username: String,
        password: String
    ): OnlineResponse<BaseData?> {
        return try {
            // get student base data
            val studentResponse = HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 5000
                }
            }.request("https://www.stundenplan24.de/$schoolId/wplan/wdatenk/SPlanKl_Basis.xml") {
                method = HttpMethod.Get
                basicAuth(username, password)
            }

            // get week base data
            val weekResponse = HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 5000
                }
            }.request("https://www.stundenplan24.de/$schoolId/wplan/wdatenk/SPlanKl_Sw1.xml") {
                method = HttpMethod.Get
                basicAuth(username, password)
            }

            OnlineResponse(
                BaseData(
                    BaseDataParserStudents(studentResponse.bodyAsText()),
                    WeekData(weekResponse.bodyAsText())
                ), Response.SUCCESS
            )
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectTimeoutException, is HttpRequestTimeoutException, is ConnectException -> return OnlineResponse(
                    null,
                    Response.NO_INTERNET
                )

                else -> {
                    Log.d("HolidayRepositoryImpl", "other error: ${e.javaClass.name} ${e.message}")
                    return OnlineResponse(null, Response.OTHER)
                }
            }
        }
    }

    override suspend fun processBaseDataStudents(baseDataParserStudents: BaseDataParserStudents) {
        // TODO implement from OnboardingViewModel
    }

    override suspend fun processBaseDataWeeks(schoolId: Long, weekData: WeekData) {
        weekData.weekDataObject.classes!!.forEach {
            val currentClass =
                classRepository.getClassBySchoolIdAndClassName(schoolId, it.schoolClass)!!
            lessonTimeRepository.deleteLessonTimes(currentClass)
            it.lessons!!.forEach lessonInsert@{ lesson ->
                if (lesson.from == "") return@lessonInsert
                lessonTimeRepository.insertLessonTime(
                    LessonTime(
                        classId = currentClass.id!!,
                        lessonNumber = lesson.lessonNumber,
                        start = lesson.from,
                        end = lesson.to
                    )
                )
            }
        }
    }
}
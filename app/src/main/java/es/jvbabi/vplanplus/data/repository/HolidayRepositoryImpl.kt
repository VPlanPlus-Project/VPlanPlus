package es.jvbabi.vplanplus.data.repository

import android.util.Log
import es.jvbabi.vplanplus.data.source.HolidayDao
import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.util.DateUtils
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.basicAuth
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod.Companion.Get
import java.net.UnknownHostException

class HolidayRepositoryImpl(
    private val holidayDao: HolidayDao
) : HolidayRepository {
    override suspend fun getHolidaysBySchoolId(schoolId: String): List<Holiday> {
        return holidayDao.getHolidaysBySchoolId(schoolId)
    }

    override suspend fun getTodayHoliday(schoolId: String): Holiday? {
        return holidayDao.getHolidaysBySchoolId(schoolId).find {
            it.timestamp == DateUtils.getCurrentDayTimestamp()
        }
    }

    override suspend fun insertHolidays(holidays: List<Holiday>) {
        holidays.map { it.schoolId }.toSet().forEach {
            holidayDao.deleteHolidaysBySchoolId(it?:"")
        }
        holidays.forEach {
            holidayDao.insertHoliday(it)
        }
    }

    override suspend fun insertHoliday(holiday: Holiday) {
        holidayDao.deleteHolidaysBySchoolId(holiday.schoolId?:"")
        holidayDao.insertHoliday(holiday)
    }

    override suspend fun deleteHolidaysBySchoolId(schoolId: String) {
        holidayDao.deleteHolidaysBySchoolId(schoolId)
    }

    override suspend fun getHolidaysBySchoolIdOnline(
        schoolId: String,
        username: String,
        password: String
    ): OnlineResponse<List<Holiday>> {
        return try {
            val response = HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 5000
                }
            }.request("https://www.stundenplan24.de/$schoolId/wplan/wdatenk/SPlanKl_Basis.xml") {
                method = Get
                basicAuth(username, password)
            }
            val baseData = BaseDataParserStudents(response.bodyAsText())
            val holidays = ArrayList<Holiday>()
            baseData.holidays.forEach {
                holidays.add(
                    Holiday(
                        schoolId = if (it.second) null else schoolId,
                        timestamp = DateUtils.getDayTimestamp(
                            year = it.first.first,
                            month = it.first.second,
                            day = it.first.third
                        )
                    )
                )

            }

            OnlineResponse(holidays, Response.SUCCESS)
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException -> return OnlineResponse(emptyList(), Response.NO_INTERNET)
                is ConnectTimeoutException -> return OnlineResponse(emptyList(), Response.NO_INTERNET)
                is HttpRequestTimeoutException -> return OnlineResponse(emptyList(), Response.NO_INTERNET)
                else -> {
                    Log.d("HolidayRepositoryImpl", "other error: ${e.javaClass.name} ${e.message}")
                    return OnlineResponse(emptyList(), Response.OTHER)
                }
            }
        }
    }
}
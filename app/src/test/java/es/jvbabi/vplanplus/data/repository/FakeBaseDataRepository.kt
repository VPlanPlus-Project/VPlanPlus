package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.repository.BaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataClass
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.BaseDataResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import java.time.LocalDate

class FakeBaseDataRepository(
    private val hasInternet: Boolean,
    private val hasRoomsAndTeachers: Boolean
) : BaseDataRepository {
    val schools = mapOf(
        10000000 to Pair("schueler", "pass")
    )

    override suspend fun checkCredentials(
        schoolId: Int,
        username: String,
        password: String
    ): DataResponse<Boolean?> {
        if (!hasInternet) return DataResponse(null, null)
        delay(100)
        val success = schools[schoolId]?.first == username && schools[schoolId]?.second == password
        if (success) return DataResponse(true, HttpStatusCode.OK)
        return DataResponse(false, HttpStatusCode.Unauthorized)
    }

    override suspend fun getBaseData(
        sp24SchoolId: Int,
        username: String,
        password: String
    ): BaseDataResponse {
        if (!hasInternet) return BaseDataResponse.Error
        if (!checkCredentials(sp24SchoolId, username, password).data!!) return BaseDataResponse.Unauthorized

        delay(100)
        val rooms = if (hasRoomsAndTeachers) listOf("A1", "A2", "A3") else null
        val teachers = if (hasRoomsAndTeachers) listOf("teacher1", "teacher2", "teacher3") else null
        return BaseDataResponse.Success(
            baseData = BaseData(
                daysPerWeek = 5,
                rooms = rooms,
                teachers = teachers,
                downloadMode = SchoolDownloadMode.INDIWARE_WOCHENPLAN_6,
                holidays = listOf(LocalDate.of(LocalDate.now().year, 12, 25)),
                classes = listOf(
                    BaseDataClass("5a", mapOf(1 to Pair("08:00", "08:45"), 2 to Pair("08:50", "09:35"))),
                    BaseDataClass("5b", mapOf(1 to Pair("08:00", "08:45"), 2 to Pair("08:50", "09:35"))),
                    BaseDataClass("5c", mapOf(1 to Pair("08:00", "08:45"), 2 to Pair("08:50", "09:35"))),
                )
            )
        )
    }
}
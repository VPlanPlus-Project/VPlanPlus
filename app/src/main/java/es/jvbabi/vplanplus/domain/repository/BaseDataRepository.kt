package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import java.time.LocalDate

interface BaseDataRepository {

    suspend fun checkCredentials(
        schoolId: Int,
        username: String,
        password: String,
    ): DataResponse<Boolean?>

    suspend fun getBaseData(
        sp24SchoolId: Int,
        username: String,
        password: String,
    ): BaseDataResponse

}

data class BaseData(
    val daysPerWeek: Int,
    val classes: List<BaseDataClass>,
    val teachers: List<String>?,
    val rooms: List<String>?,
    val holidays: List<LocalDate>,
    val downloadMode: SchoolDownloadMode,
    val canUseTimetable: Boolean
)

data class BaseDataClass(
    val name: String,
    val lessonTimes: Map<Int, Pair<String, String>>
)

sealed class BaseDataResponse {
    data object Unauthorized : BaseDataResponse()
    data class Success(val baseData: BaseData) : BaseDataResponse()
    data object Error : BaseDataResponse()
}
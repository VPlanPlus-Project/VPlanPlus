package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.model.WPlanBaseData
import java.time.LocalDate

interface BaseDataRepository {

    suspend fun getFullBaseData(
        sp24SchoolId: Int,
        username: String,
        password: String,
    ): DataResponse<WPlanBaseData?>

    suspend fun checkCredentials(
        schoolId: Int,
        username: String,
        password: String,
    ): DataResponse<Boolean?>

    suspend fun getBaseData(
        sp24SchoolId: Int,
        username: String,
        password: String,
    ): BaseData?

}

data class BaseData(
    val daysPerWeek: Int,
    val classes: List<BaseDataClass>,
    val teachers: List<String>?,
    val rooms: List<String>?,
    val holidays: List<LocalDate>,
    val downloadMode: SchoolDownloadMode
)

data class BaseDataClass(
    val name: String,
    val lessonTimes: Map<Int, Pair<String, String>>
)
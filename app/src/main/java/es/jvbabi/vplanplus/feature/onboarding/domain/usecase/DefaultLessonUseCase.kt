package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.gson.Gson
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.Response
import java.time.LocalDate

class DefaultLessonUseCase(
    private val vPlanRepository: VPlanRepository,
    private val kv: KeyValueRepository
) {

    suspend operator fun invoke(schoolId: Long, username: String, password: String, index: Int = -7, className: String): List<DefaultLesson>? {
        if (index > 7) return null

        val gson = Gson()

        val vPlanData = vPlanRepository.getVPlanData(
            School(
                schoolId = schoolId,
                username = username,
                password = password,
                name = "",
                daysPerWeek = 5,
                fullyCompatible = true
            ),
            LocalDate.now().plusDays(index.toLong())
        )
        if (vPlanData.response != Response.SUCCESS) return invoke(schoolId, username, password, index + 1, className)

        val defaultLessons = vPlanData.data!!.wPlanDataObject.classes!!.map { c ->
            c.defaultLessons!!.map {
                DefaultLesson(
                    vpId = it.defaultLesson!!.lessonId!!.toLong(),
                    className = c.schoolClass,
                    subject = it.defaultLesson!!.subjectShort!!,
                    teacher = it.defaultLesson!!.teacherShort!!
                )
            }
        }.flatten().filter { it.className == className }
        kv.set("onboarding.school.$schoolId.defaultLessons", gson.toJson(defaultLessons))
        return defaultLessons
    }
}

data class DefaultLesson(
    val vpId: Long,
    val className: String,
    val subject: String,
    val teacher: String
)
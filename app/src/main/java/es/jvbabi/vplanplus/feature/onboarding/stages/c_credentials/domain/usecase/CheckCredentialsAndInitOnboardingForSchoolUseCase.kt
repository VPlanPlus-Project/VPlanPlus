package es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase

import android.os.Parcelable
import es.jvbabi.vplanplus.data.serializers.LocalDateSerializer
import es.jvbabi.vplanplus.domain.model.SchoolSp24Access
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import kotlinx.coroutines.flow.first
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class CheckCredentialsAndInitOnboardingForSchoolUseCase(
    private val schoolRepository: SchoolRepository,
    private val baseDataRepository: BaseDataRepository,
    private val profileRepository: ProfileRepository,
    private val vPlanRepository: VPlanRepository,
    private val vppIdRepository: VppIdRepository,
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(sp24SchoolId: Int, username: String, password: String): OnboardingInit? {
        val school = schoolRepository.getSchoolBySp24Id(sp24SchoolId)

        val baseData = baseDataRepository.getBaseData(sp24SchoolId, username, password) ?: return null
        val isFirstProfile = school == null || profileRepository.getProfilesBySchool(school.id).first().isEmpty()

        val schoolInformation = schoolRepository.getSchoolInfoBySp24DataOnline(sp24SchoolId, username, password) ?: return null

        val getDefaultLessons: suspend (date: LocalDate) -> List<OnboardingDefaultLesson>? = { date: LocalDate ->
            val result = vPlanRepository.getVPlanData(sp24SchoolId, username, password, date, baseData.downloadMode)
            result.data
                ?.wPlanDataObject
                ?.classes
                ?.mapNotNull clazz@{ clazz ->
                    clazz.defaultLessons?.mapNotNull defaultLesson@{ dl ->
                        OnboardingDefaultLesson(
                            clazz = clazz.schoolClass,
                            subject = dl.defaultLesson?.subjectShort ?: return@defaultLesson null,
                            teacher = dl.defaultLesson?.teacherShort ?: return@defaultLesson null,
                            vpId = dl.defaultLesson?.lessonId ?: return@defaultLesson null,
                            courseGroup = dl.defaultLesson?.courseGroup
                        )
                    }
                }
                ?.flatten()
        }

        var defaultLessons: List<OnboardingDefaultLesson>? = null
        var useDate = LocalDate.now()
        while (defaultLessons == null && LocalDate.now().until(useDate).days < 7) {
            defaultLessons = getDefaultLessons(useDate)
            useDate = useDate.plusDays(1)
        }
        if (defaultLessons == null) {
            useDate = LocalDate.now()
            while (defaultLessons == null && LocalDate.now().until(useDate).days > -7) {
                defaultLessons = getDefaultLessons(useDate)
                useDate = useDate.minusDays(1)
            }
        }
        val groups = vppIdRepository.fetchUsersPerClass(
            SchoolSp24Access(schoolId = schoolInformation.schoolId, sp24SchoolId = sp24SchoolId, username = username, password = password)
        ) ?: return null

        val isFullySupported = baseData.teachers != null && baseData.rooms != null

        val classes = baseData.classes.mapNotNull { schoolClass ->
            val entry = groups.find { c -> c.className == schoolClass.name } ?: return@mapNotNull null
            val lessonTimes = schoolClass.lessonTimes
                .toList()
                .map { (lessonNumber, times) ->
                    LessonTime(
                        lessonNumber = lessonNumber,
                        startHour = times.first.substringBefore(":").toInt(),
                        startMinute = times.first.substringAfter(":").toInt(),
                        endHour = times.second.substringBefore(":").toInt(),
                        endMinute = times.second.substringAfter(":").toInt()
                    )
                }
            OnboardingInitClass(schoolClass.name, entry.groupId, entry.users, lessonTimes)
        }

        keyValueRepository.set("onboarding.sp24_school_id", sp24SchoolId.toString())
        keyValueRepository.set("onboarding.username", username)
        keyValueRepository.set("onboarding.password", password)
        keyValueRepository.set("onboarding.is_fully_supported", isFullySupported.toString())
        keyValueRepository.set("onboarding.is_first_profile", isFirstProfile.toString())
        keyValueRepository.set("onboarding.school_id", schoolInformation.schoolId.toString())
        keyValueRepository.set("onboarding.school_name", schoolInformation.name)
        keyValueRepository.set("onboarding.days_per_week", baseData.daysPerWeek.toString())
        keyValueRepository.set("onboarding.default_lessons", Json.encodeToString(defaultLessons.orEmpty()))
        keyValueRepository.set("onboarding.classes", Json.encodeToString(classes))
        keyValueRepository.set("onboarding.teachers", Json.encodeToString(baseData.teachers.orEmpty()))
        keyValueRepository.set("onboarding.rooms", Json.encodeToString(baseData.rooms.orEmpty()))
        keyValueRepository.set("onboarding.holidays", Json.encodeToString(baseData.holidays.map { Holiday(it, false) }))
        keyValueRepository.set("onboarding.download_mode", baseData.downloadMode.name)

        return OnboardingInit(
            fullySupported = isFullySupported,
            isFirstProfile = isFirstProfile,
            schoolId = schoolInformation.schoolId,
            name = schoolInformation.name,
            daysPerWeek = baseData.daysPerWeek,
            defaultLessons = defaultLessons.orEmpty(),
            classes = classes,
            teachers = baseData.teachers.orEmpty(),
            rooms = baseData.rooms.orEmpty(),
            holidays = baseData.holidays.map { Holiday(it, false) }
        )
    }
}

@Serializable
@Parcelize
data class OnboardingInit(
    @SerialName("is_fully_supported") val fullySupported: Boolean,
    @SerialName("is_first_profile") val isFirstProfile: Boolean,
    @SerialName("school_id") val schoolId: Int = -1,
    @SerialName("name") val name: String = "",
    @SerialName("days_per_week") val daysPerWeek: Int = 5,
    @SerialName("classes") val classes: List<OnboardingInitClass> = emptyList(),
    @SerialName("teachers") val teachers: List<String> = emptyList(),
    @SerialName("rooms") val rooms: List<String> = emptyList(),
    @SerialName("default_lessons") val defaultLessons: List<OnboardingDefaultLesson> = emptyList(),
    @SerialName("are_credentials_correct") val areCredentialsCorrect: Boolean = true,
    @SerialName("is_school_supported") val isSchoolSupported: Boolean = true,
    @SerialName("holidays") val holidays: List<Holiday> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class OnboardingDefaultLesson(
    @SerialName("class") val clazz: String,
    @SerialName("subject") val subject: String,
    @SerialName("teacher") val teacher: String?,
    @SerialName("vp_id") val vpId: Int,
    @SerialName("course_group") val courseGroup: String? = null
) : Parcelable

@Serializable
@Parcelize
data class OnboardingInitClass(
    @SerialName("name") val name: String,
    @SerialName("id") val id: Int,
    @SerialName("users") val users: Int,
    @SerialName("lesson_times") val lessonTimes: List<LessonTime>
) : Parcelable

@Serializable
@Parcelize
data class Holiday(
    @Serializable(with = LocalDateSerializer::class) val date: LocalDate,
    @SerialName("is_public") val isPublic: Boolean = true
) : Parcelable

@Serializable
@Parcelize
data class LessonTime(
    @SerialName("lesson_number") val lessonNumber: Int,
    @SerialName("start_hour") val startHour: Int,
    @SerialName("start_minute") val startMinute: Int,
    @SerialName("end_hour") val endHour: Int,
    @SerialName("end_minute") val endMinute: Int
) : Parcelable
